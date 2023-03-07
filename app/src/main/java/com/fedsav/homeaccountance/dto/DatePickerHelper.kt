package com.fedsav.homeaccountance.dto

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class DatePickerHelper {

    private val DATE_PRESENTATION_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    private val myCalendar: Calendar = Calendar.getInstance()
    private var dateTime: LocalDateTime = LocalDateTime.now()

    var year: Int = myCalendar.get(Calendar.YEAR)
    var month: Int = myCalendar.get(Calendar.MONTH)
    var day: Int = myCalendar.get(Calendar.DAY_OF_MONTH)

    fun getPresentationDate(): String {
        return dateTime.format(DATE_PRESENTATION_FORMATTER)
    }

    fun modifyDateFromPresentation(inputDate : String) {
        dateTime = getCurrentDateTimeFromDate(LocalDate.parse(inputDate, DATE_PRESENTATION_FORMATTER))
    }

    fun modifyDateFromInput(year: Int, month: Int, day: Int) {
        dateTime = getCurrentDateTimeFromDate(LocalDate.of(year, month, day))
    }

    private fun getCurrentDateTimeFromDate(inputDate: LocalDate) =
        LocalDateTime.of(inputDate, LocalTime.now())


    fun getJsonDate(): String {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

}