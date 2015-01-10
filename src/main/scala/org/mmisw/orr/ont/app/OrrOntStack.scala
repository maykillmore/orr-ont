package org.mmisw.orr.ont.app

import com.mongodb.casbah.Imports._
import org.json4s.JsonAST.{JArray, JNothing, JString, JValue}
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import org.mmisw.orr.ont.swld.ontUtil
import org.scalatra._
import org.scalatra.json.NativeJsonSupport


trait OrrOntStack extends ScalatraServlet with NativeJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  protected val dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  protected def error(status: Int, msg: String): Nothing = halt(status, MongoDBObject("error" -> msg))

  protected def error(status: Int, details: Seq[(String,String)]): Nothing = halt(status, MongoDBObject(details: _*))

  protected def missing(paramName: String): Nothing = error(400, s"'$paramName' param missing")

  protected def bug(msg: String): Nothing = error(500, s"$msg. Please notify this bug.")

  protected def require(map: Params, paramName: String) = {
    val value = map.getOrElse(paramName, missing(paramName)).trim
    if (value.length > 0) value else error(400, s"'$paramName' param value missing")
  }

  protected def acceptOnly(paramNames: String*) {
    val unrecognized = params.keySet -- Set(paramNames: _*)
    if (unrecognized.size > 0) error(400, s"unrecognized parameters: $unrecognized")
  }

  protected def body(): Map[String, JValue] = {
    val json = parse(request.body)
    if (json != JNothing) json.extract[Map[String, JValue]] else error(400, "missing json body")
  }

  protected def require(map: Map[String, JValue], paramName: String) = {
    val value = map.getOrElse(paramName, missing(paramName))
    if (!value.isInstanceOf[JString]) error(400, s"'$paramName' param value is not a string")
    val str = value.asInstanceOf[JString].values.trim
    if (str.length > 0) str else error(400, s"'$paramName' param value missing")
  }

  protected def getString(map: Map[String, JValue], paramName: String): Option[String] = {
    map.get(paramName) map {value =>
      if (!value.isInstanceOf[JString]) error(400, s"'$paramName' param value is not a string")
      value.asInstanceOf[JString].values
    }
  }

  protected def getSeq(map: Map[String, JValue], paramName: String, canBeEmpty: Boolean = false): List[String] = {
    val value = map.getOrElse(paramName, missing(paramName))
    if (!value.isInstanceOf[JArray]) error(400, s"'$paramName' param value is not an array")
    val arr = value.asInstanceOf[JArray].arr
    if (!canBeEmpty && arr.length == 0) error(400, s"'$paramName' array param cannot be empty")
    arr map (_.asInstanceOf[JString].values)
  }

  ontUtil.mimeMappings foreach { xm => addMimeMapping(xm._2, xm._1) }

  // todo why we seem to need to re-set this default one? otherwise tests break!
  addMimeMapping("application/json", "json")

  before() {
    contentType = formats("json")
  }
}