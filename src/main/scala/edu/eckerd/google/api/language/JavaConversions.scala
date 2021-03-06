package edu.eckerd.google.api.language

import com.google.api.services.admin.{directory => jDirectory}
import com.google.api.services.{calendar => jCalendar, drive => jDrive}
import edu.eckerd.google.api.services.{directory => sDirectory}
import edu.eckerd.google.api.services.{calendar => sCalendar, drive => sDrive}

import scala.language.{implicitConversions, postfixOps}

/**
  * Created by davenpcm on 5/7/16.
  */
object JavaConversions {

  /**
    * This is a one way conversion from the scala object to the java Directory. The java directory does not have
    * credentials or any of the other inherent attributes. So we use the internal to create what is needed to create
    * the java Directory
    *
    * @param b A Scala Directory
    * @return A Java Directory
    */
  implicit def scalaDirectoryAsJavaDirectoryConversion(b: edu.eckerd.google.api.services.directory.Directory): jDirectory.Directory = {
    new com.google.api.services.admin.directory.Directory.Builder(
      b.httpTransport,
      b.jsonFactory,
      b.credential)
      .setApplicationName(b.applicationName)
      .setHttpRequestInitializer(b.credential)
      .build()
  }

  /**
    * This converts the Scala Class to the Java Class. Checks whether options are Defined and only applies what
    * is necessary to the class. As Members was a field added in this wrapper it is not converted back.
    *
    * @param b A Scala Group
    * @return A Java Group
    */
  implicit def scalaGroupAsJavaGroupConversion(b: edu.eckerd.google.api.services.directory.models.Group): jDirectory.model.Group = {
    val newGroup = new jDirectory.model.Group()
      .setName(b.name)
      .setEmail(b.email)

    if (b.id isDefined) { newGroup.setId(b.id.get) }
    if (b.description isDefined) { newGroup.setDescription(b.description.get) }
    if (b.directMemberCount isDefined){ newGroup.setDirectMembersCount(b.directMemberCount.get)}
    if (b.adminCreated isDefined){ newGroup.setAdminCreated(b.adminCreated.get)}

    newGroup
  }

  /**
    * Converts a Java Group to a Scala Group. As Groups from the API never discuss members this is initialized as None
    * and all other fields are based on whether the fields are present or not. ID is a some because despite always being
    * returned from google, it is not known when you create a new group so should therefore have the option to be
    * uninitialized.
    * @param b A Java Group
    * @return A Scala Group
    */
  implicit def javaGroupAsScalaGroupConversion(b: jDirectory.model.Group): edu.eckerd.google.api.services.directory.models.Group = {
    sDirectory.models.Group(
      if (b.getName != null) b.getName else throw new Throwable("Null Group Name Error"),
      if (b.getEmail != null) b.getEmail else throw new Throwable("Null Group Email Error"),
      Option(b.getId),
      Option(b.getDescription),
      if (b.getDirectMembersCount != null) Some(b.getDirectMembersCount) else None,
      None,
      if (b.getAdminCreated != null) Some(b.getAdminCreated) else None
    )
  }

  /**
    * Converts a Scala Groups Object to a Java Groups Object. The Java Groups Object is returned by a call to the groups
    * api and is important as it is more of a page of groups, with a list of the groups and a page token to continue to
    * the next page. This scala object allows for us to use this paging in scala, and transform back if necessary.
    * @param b A Scala Groups
    * @return A Java Groups
    */
  implicit def scalaGroupsAsJavaGroupsConversion(b: edu.eckerd.google.api.services.directory.models.Groups): jDirectory.model.Groups = {
    import collection.JavaConverters._
    val groups = b.groups.getOrElse(List[edu.eckerd.google.api.services.directory.models.Group]()).map(scalaGroupAsJavaGroupConversion).asJava
    val pageToken = b.nextPageToken.orNull

    new jDirectory.model.Groups()
      .setGroups(groups)
      .setNextPageToken(pageToken)
  }

  /**
    * Conversts a Java Groups Object to a Scala Groups Object. Hopefully this is only used to page through the groups
    * as otherwise it is primarily  just a List of Groups
    * @param b A Java Groups
    * @return A Scala Groups
    */
  implicit def javaGroupsAsScalaGroupsConversion(b: jDirectory.model.Groups): edu.eckerd.google.api.services.directory.models.Groups = {
    import collection.JavaConverters._
    val groups = Option(b.getGroups).map(_.asScala.toList.map(javaGroupAsScalaGroupConversion))
    val pageToken = Option(b.getNextPageToken)
    sDirectory.models.Groups(
      groups,
      pageToken
    )
  }

  implicit def scalaMemberAsJavaMemberConversion(b: edu.eckerd.google.api.services.directory.models.Member): jDirectory.model.Member = {
    val newMember = new com.google.api.services.admin.directory.model.Member()
      .setRole(b.role)
      .setType(b.memberType)

    if (b.email isDefined){ newMember.setEmail(b.email.get)}
    if (b.id isDefined){ newMember.setId(b.id.get)}

    newMember
  }

  implicit def javaMemberAsScalaMemberConversion(member: jDirectory.model.Member): edu.eckerd.google.api.services.directory.models.Member = {
    sDirectory.models.Member(
      Option(member.getEmail),
      Option(member.getId),
      member.getRole,
      member.getType
    )
  }

  implicit def scalaMembersAsJavaMembersConversion(b: edu.eckerd.google.api.services.directory.models.Members): jDirectory.model.Members = {
    import scala.collection.JavaConverters._
    val ListJava: List[jDirectory.model.Member] = b.members.get.map(scalaMemberAsJavaMemberConversion)

    new jDirectory.model.Members()
      .setMembers(ListJava.asJava)
      .setNextPageToken(b.nextPageToken.get)
  }

  implicit def javaMembersAsScalaMembersConversion(b: jDirectory.model.Members): edu.eckerd.google.api.services.directory.models.Members = {
    import scala.collection.JavaConverters._
    sDirectory.models.Members(
      Option(b.getMembers)
        .map(_.asScala.toList.map(javaMemberAsScalaMemberConversion)),
      Option(b.getNextPageToken)
    )
  }

  implicit def scalaUserAsJavaUserConversion(user: edu.eckerd.google.api.services.directory.models.User): jDirectory.model.User = {
    val newUser = new com.google.api.services.admin.directory.model.User
    newUser
      .setChangePasswordAtNextLogin(user.changePasswordAtNextLogin)
      .setIncludeInGlobalAddressList(user.includeInGlobalAddressList)
      .setIpWhitelisted(user.ipWhiteListed)
      .setIsAdmin(user.isAdmin)
      .setIsMailboxSetup(user.isMailboxSetup)
      .setSuspended(user.suspended)
      .setOrgUnitPath(user.orgUnitPath)
      .setPrimaryEmail(user.primaryEmail)
      .setName(user.name)

    if (user.agreedToTerms isDefined) { newUser.setAgreedToTerms( user.agreedToTerms.get)}
    if (user.password isDefined) { newUser.setPassword( user.password.get)}


    newUser
  }

  implicit def javaUserAsScalaUserConversion(user: jDirectory.model.User): edu.eckerd.google.api.services.directory.models.User = {
    sDirectory.models.User(
      user.getName,
      sDirectory.models.Email(user.getPrimaryEmail),
      Option(user.getPassword),
      Option(user.getId),
      user.getOrgUnitPath,
      Option(user.getAgreedToTerms) match {
        case Some(value ) => Some(value)
        case None => None
      },
      user.getChangePasswordAtNextLogin,
      user.getIncludeInGlobalAddressList,
      user.getIpWhitelisted,
      user.getIsAdmin,
      user.getIsMailboxSetup,
      user.getSuspended
    )
  }

  implicit def scalaUsersAsJavaUsersConversion(b: edu.eckerd.google.api.services.directory.models.Users): jDirectory.model.Users = {
    import scala.collection.JavaConverters._
    val users = b.users.map(_.map(scalaUserAsJavaUserConversion).asJava)

    new jDirectory.model.Users()
      .setUsers(users.orNull)
      .setNextPageToken(b.nextPageToken.orNull)
  }

  implicit def javaUsersAsScalaUsersConversion(b: jDirectory.model.Users): edu.eckerd.google.api.services.directory.models.Users = {
    import scala.collection.JavaConverters._
    val users = Option(b.getUsers).map(_.asScala.toList.map(javaUserAsScalaUserConversion))
    val pageToken = Option(b.getNextPageToken)
    sDirectory.models.Users(
      users,
      pageToken
    )
  }

  implicit def scalaNameAsJavaUserNameConversion(name: edu.eckerd.google.api.services.directory.models.Name): jDirectory.model.UserName = {
    new com.google.api.services.admin.directory.model.UserName()
      .setGivenName(name.givenName)
      .setFamilyName(name.familyName)
  }

  implicit def javaUserNameAsScalaNameConversion(userName: jDirectory.model.UserName): edu.eckerd.google.api.services.directory.models.Name = {
    sDirectory.models.Name(
      userName.getGivenName,
      userName.getFamilyName
    )
  }

  implicit def scalaEmailAsJavaUserEmailConversion(email: edu.eckerd.google.api.services.directory.models.Email): jDirectory.model.UserEmail = {
    new com.google.api.services.admin.directory.model.UserEmail()
      .setAddress(email.address)
      .setPrimary(email.primary)
  }

  implicit def javaUserEmailAsScalaEmailConversion(userEmail: jDirectory.model.UserEmail): edu.eckerd.google.api.services.directory.models.Email = {
    sDirectory.models.Email(
      userEmail.getAddress,
      userEmail.getPrimary
    )
  }

  implicit def scalaDriveAsJavaDriveConversion(b: edu.eckerd.google.api.services.drive.Drive): jDrive.Drive = {
    new com.google.api.services.drive.Drive.Builder(
      b.httpTransport,
      b.jsonFactory,
      b.credential)
      .setApplicationName(b.applicationName)
      .setHttpRequestInitializer(b.credential)
      .build()
  }

  implicit def scalaFileContentAsJavaFileContentConversion(fileContent: edu.eckerd.google.api.services.drive.models.FileContent)
  : com.google.api.client.http.FileContent = {
    new com.google.api.client.http.FileContent(fileContent.mimeType, fileContent.content)
  }

//  implicit def scalaFileAsJavaFileConversion(file: edu.eckerd.google.api.services.drive.models.File): jDrive.model.File = {
//    import scala.collection.JavaConverters._
//    val metadata = new com.google.api.services.drive.model.File()
//      .setName(file.name)
//      .setMimeType(file.mimeType)
//      .setId(file.id)
////    if (file.extension isDefined){ metadata.setFileExtension(file.extension.get)}
////    if (file.description isDefined){ metadata.setDescription(file.description.get)}
//    if (file.parentIds isDefined){ metadata.setParents(file.parentIds.get.asJava)}
//    file.trashed.map(trashed => metadata.setExplicitlyTrashed(trashed))
//    metadata
//  }

//  implicit def javaFileAsScalaFileConversion(file: jDrive.model.File): edu.eckerd.google.api.services.drive.models.File = {
//    sDrive.models.File(
//      file.getName,
//      file.getMimeType,
//      file.getId,
//      Option(file.getCreatedTime),
//      Option(file.getModifiedTime),
//      if (file.getTrashed != null) Some(file.getTrashed) else None,
//      Option(file.getParents).map{ parents =>
//        import scala.collection.JavaConverters._
//        parents.asScala.toList
//      }
//    )
//  }

//  implicit def scalaListFileAsJavaFileListConversion(b: List[edu.eckerd.google.api.services.drive.models.File]): jDrive.model.FileList = {
//    import collection.JavaConverters._
//    val files = b.map(scalaFileAsJavaFileConversion).asJava
//    new jDrive.model.FileList()
//      .setFiles(files)
//  }
//
//  implicit def javaFileListAsScalaListFileConversion(b: jDrive.model.FileList): List[edu.eckerd.google.api.services.drive.models.File] = {
//    import collection.JavaConverters._
//    b.getFiles.asScala.toList.map(javaFileAsScalaFileConversion)
//  }

  implicit def scalaPermissionAsJavaPermissionConversion(permission: edu.eckerd.google.api.services.drive.models.Permission)
  : jDrive.model.Permission = {

    val Permission = new com.google.api.services.drive.model.Permission()
      .setRole(permission.role)
      .setType(permission.permissionType)

    if (permission.emailAddress isDefined){ Permission.setEmailAddress(permission.emailAddress.get) }
    if (permission.displayName isDefined){ Permission.setDisplayName(permission.displayName.get) }
    if (permission.id isDefined) { Permission.setId(permission.id.get)}
    Permission
  }

  implicit def javaPermissionAsScalaPermissionConversion(permission: jDrive.model.Permission)
  : edu.eckerd.google.api.services.drive.models.Permission = {
    sDrive.models.Permission(
      permission.getRole,
      permission.getType,
      Option(permission.getEmailAddress),
      Option(permission.getDisplayName),
      Option(permission.getId)
    )
  }

  implicit def scalaListPermissionAsJavaPermissionsListConversion(b: List[edu.eckerd.google.api.services.drive.models.Permission])
  : jDrive.model.PermissionList = {
    import scala.collection.JavaConverters._
    val javaPermissions = b.map(scalaPermissionAsJavaPermissionConversion)
    new jDrive.model.PermissionList()
      .setPermissions(javaPermissions.asJava)
  }

  implicit def javaPermissionsListAsScalaListPermissionsConversion(b: jDrive.model.PermissionList)
  : List[edu.eckerd.google.api.services.drive.models.Permission] = {
    import scala.collection.JavaConverters._
    b.getPermissions.asScala.toList.map(javaPermissionAsScalaPermissionConversion)
  }

  implicit def scalaCalendarAsJavaCalendarConversion(service: edu.eckerd.google.api.services.calendar.Calendar): jCalendar.Calendar = {
    new com.google.api.services.calendar.Calendar.Builder(service.httpTransport, service.jsonFactory, service.credential)
      .setApplicationName(service.applicationName)
      .setHttpRequestInitializer(service.credential)
      .build()
  }

  implicit def scalaEventAsJavaEventConversion(b: edu.eckerd.google.api.services.calendar.models.Event): jCalendar.model.Event = {
    import collection.JavaConverters._
    val event = new jCalendar.model.Event


    val participants = b.participantEmails
      .map(
        _.map(
          email => new jCalendar.model.EventAttendee().setEmail(email)
        )
      )

    event.setSummary(b.title)
    if (b.participantEmails isDefined) {event.setDescription(b.description.get)}
    if (b.participantEmails isDefined) {event.setAttendees(participants.get.asJava)}
    if (b.startTime isDefined){ event.setStart(b.startTime.get) }
    if (b.endTime isDefined) { event.setEnd(b.endTime.get) }
    if (b.recurrence isDefined){ event.setRecurrence(b.recurrence.get.asJava) }
    if (b.id isDefined){ event.setId(b.id.get)}
    event
  }

  implicit def javaEventAsScalaEventConversion(b: jCalendar.model.Event): edu.eckerd.google.api.services.calendar.models.Event = {
    import collection.JavaConverters._
    sCalendar.models.Event(
      b.getSummary,
      {if(b.getDescription != "") Some(b.getDescription) else None},

      {if (b.getStart.getDateTime != null)
        Some(b.getStart.getDateTime)
      else None },

      {if (b.getEnd.getDateTime != null)
        Some(b.getEnd.getDateTime)
      else None },

      Option(b.getAttendees)
        .map(attendees => attendees.asScala.toList)
        .map(listOfAttendees => listOfAttendees.map(_.getEmail)),
      Option(b.getRecurrence).map(_.asScala.toList),
      Option(b.getId)
    )
  }

  implicit def javaZonedDateTimeAsGoogleDateTimeConversion(b: java.time.ZonedDateTime)
  : com.google.api.client.util.DateTime = {

    new com.google.api.client.util.DateTime(b.toOffsetDateTime.toString)
  }

  implicit def googleDateTimeAsJavaZoneDateTimeConversion(b: com.google.api.client.util.DateTime)
  : java.time.ZonedDateTime = {
    java.time.ZonedDateTime.parse(b.toStringRfc3339.toCharArray)
  }

  implicit def javaZonedDateTimeAsGoogleEventDateTimeConversion(b: java.time.ZonedDateTime)
  : jCalendar.model.EventDateTime = {

    new jCalendar.model.EventDateTime()
      .setDateTime(b)
      .setTimeZone(b.getZone.toString)
  }

  implicit def googleEventDateTimeAsJavaZonedDateTimeConversion(b: jCalendar.model.EventDateTime)
  : java.time.ZonedDateTime = {

    val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
    java.time.ZonedDateTime.parse(b.getDateTime.toStringRfc3339.toCharArray, formatter)
  }


}
