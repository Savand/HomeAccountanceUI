package com.fedsav.homeaccountance.dto

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class DateFormatter(editDateTime: String?) {

    private val DATE_PRESENTATION_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    private var dateTime: LocalDateTime

    var year: Int
    var month: Int
    var day: Int

    init {

        dateTime = if (editDateTime == null) {
            LocalDateTime.now()
        } else {
            LocalDateTime.parse(editDateTime, DateTimeFormatter.ISO_DATE_TIME)
        }

        year = dateTime.year
        month = dateTime.monthValue - 1
        day = dateTime.dayOfMonth

    }

    fun getPresentationDate(): String {
        return dateTime.format(DATE_PRESENTATION_FORMATTER)
    }

    fun modifyDateFromPresentation(inputDate: String) {
        dateTime =
            getCurrentDateTimeFromDate(LocalDate.parse(inputDate, DATE_PRESENTATION_FORMATTER))
    }

    fun modifyDateFromInput(year: Int, month: Int, day: Int) {
        dateTime = getCurrentDateTimeFromDate(LocalDate.of(year, month + 1, day))
    }

    private fun getCurrentDateTimeFromDate(inputDate: LocalDate) =
        LocalDateTime.of(inputDate, LocalTime.now())


    fun getJsonDate(): String {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

}