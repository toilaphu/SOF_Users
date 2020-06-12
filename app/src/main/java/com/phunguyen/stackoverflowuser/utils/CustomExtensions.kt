package com.phunguyen.stackoverflowuser.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

fun Long.toFormattedDate(): String = DateTimeFormat.forPattern(DATE_FORMAT).print(
    DateTime(this, DateTimeZone.UTC)
)
