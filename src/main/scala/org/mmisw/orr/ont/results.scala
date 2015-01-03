package org.mmisw.orr.ont

import org.joda.time.DateTime


case class UserResult(
            userName:    String,
            registered:  Option[DateTime] = None,
            updated:     Option[DateTime] = None,
            removed:     Option[DateTime] = None)

case class PendUserResult(
            userName:    String,
            ontUri:      Option[String] = None,
            registered:  Option[DateTime] = None)

case class OrgResult(
            orgName:     String,
            registered:  Option[DateTime] = None,
            updated:     Option[DateTime] = None,
            removed:     Option[DateTime] = None,
            members:     List[String] = List.empty)

case class PendOrgResult(
            orgName:     String,
            name:        String,
            ontUri:      Option[String] = None,
            registered:  Option[DateTime] = None)

case class OntologyResult(
            uri:         String,
            version:     Option[String] = None,
            registered:  Option[DateTime] = None,
            updated:     Option[DateTime] = None,
            removed:     Option[DateTime] = None)

case class PendOntologyResult(
            uri:            String,
            name:           String,
            orgName:        Option[String] = None,
            versions:       List[String])

case class VersionInfo(
           uri:         String,
           name:        String,
           version:     String,
           date:        String,
           metadata:    Map[String,AnyRef] = Map())
