package com.eventory.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object CalendarHelper {

    /**
     * Add an event to the device calendar using an Intent
     * This approach doesn't require WRITE_CALENDAR permission
     */
    fun addEventToCalendar(
        context: Context,
        title: String,
        description: String?,
        location: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val startMillis = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description ?: "")
            putExtra(CalendarContract.Events.EVENT_LOCATION, location ?: "")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        }

        context.startActivity(intent)
    }

    /**
     * Check if a calendar is available on the device
     */
    fun isCalendarAvailable(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
        }
        return intent.resolveActivity(context.packageManager) != null
    }
}
