package org.mmisw.orr.ont.service

import java.io.File
import java.util.{Timer, TimerTask}

import com.typesafe.scalalogging.{StrictLogging ⇒ Logging}
import org.mmisw.orr.ont.util.IEmailer

import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal

trait INotifier {
  def sendNotificationEmail(subject: String, msg: String): Unit
  def destroy(): Unit
}

private case class Item(subject: String, msg: String)

class Notifier(configDir: File, emailer: IEmailer) extends INotifier with Logging {

  def sendNotificationEmail(subject: String, msg: String): Unit = {
    logger.debug(s"sendNotificationEmail: adding to queue item subject=$subject")
    queue.synchronized { queue += Item(subject, msg) }
  }

  def destroy(): Unit = {
    dispatcher.cancel()
  }

  private val SendPeriod = 5*60*1000 // 5 minutes
  private val CheckPeriod = 30*1000  // 30 secs
  private val queue = new ListBuffer[Item]
  private var latestSendTime: Long = 0

  private val timer = new Timer()
  private val dispatcher = new TimerTask {
    def run(): Unit = {
      val itemsOpt = queue.synchronized {
        if (queue.nonEmpty && System.currentTimeMillis - latestSendTime >= SendPeriod) {
          latestSendTime = System.currentTimeMillis
          val items = queue.toList
          queue.clear()
          Some(items)
        }
        else None
      }
      itemsOpt foreach dispatchItems
    }
  }

  timer.schedule(dispatcher, CheckPeriod, CheckPeriod)

  private def dispatchItems(items: List[Item]): Unit = {
    logger.debug(s"dispatchItems: ${items.size}")
    val file = new File(configDir, "notifyemails")
    for {
      emails ← getEmails(file)
      if emails.nonEmpty
    } {
      val (subject, msg) = if (items.size == 1) {
        val item = items.head
        (item.subject, item.msg)
      }
      else {
        ("Notifications", items.map(_.msg).mkString("\n\n"))
      }
      emailer.sendEmail(emails.mkString(","), subject,
        msg + "\n\n" +
          "(You have received this email because your address is" +
          " included in " +file.getAbsolutePath + ")"
      )
    }
  }

  private def getEmails(file: File): Option[Seq[String]] = {
    try {
      val source = io.Source.fromFile(file)
      val emails = source.getLines.map(_.trim).filterNot { line ⇒
        line.isEmpty || line.startsWith("#")
      }
      Some(emails.toSeq)
    }
    catch {
      case _:java.io.FileNotFoundException ⇒
        logger.warn(s"sendNotificationEmail: file not found: $file")
        None
      case NonFatal(e) ⇒
        logger.error("error getting emails", e)
        None
    }
  }
}
