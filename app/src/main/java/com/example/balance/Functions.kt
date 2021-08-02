package com.example.balance

import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

enum class Case {
    NONE, // январь
    OF, // января
    IN, // январе
    SHORT // янв
}

fun getTime(time: String): String {
    val timeOffset = 3
    val timeObj = LocalTime.parse(time)
    val currentHour = timeObj.hour + timeOffset
    val currentMinute = if (timeObj.minute < 10) "0${timeObj.minute}" else timeObj.minute.toString()
    return "$currentHour:$currentMinute"
}

//fun getTimeDifference(time: String,) {
//    val
//}


fun getWeekDay(dayNumber: Int): String {
    return when (dayNumber) {
        1 -> "пн"
        2 -> "вт"
        3 -> "ср"
        4 -> "чт"
        5 -> "пт"
        6 -> "сб"
        7 -> "вс"
        else -> ""
    }
}

fun getMonthName(monthNumber: Int, case: Case): String {
    return when (monthNumber) {
        1 -> {
            when (case) {
                Case.NONE -> "январь"
                Case.OF -> "января"
                Case.IN -> "январе"
                Case.SHORT -> "янв"
            }
        }
        2 -> {
            when (case) {
                Case.NONE -> "февраль"
                Case.OF -> "февраля"
                Case.IN -> "феврале"
                Case.SHORT -> "фев"
            }
        }
        3 -> {
            when (case) {
                Case.NONE -> "март"
                Case.OF -> "марта"
                Case.IN -> "марте"
                Case.SHORT -> "мар"
            }
        }
        4 -> {
            when (case) {
                Case.NONE -> "апрель"
                Case.OF -> "апреля"
                Case.IN -> "апреле"
                Case.SHORT -> "апр"
            }
        }
        5 -> {
            when (case) {
                Case.NONE -> "май"
                Case.OF -> "мае"
                Case.IN -> "мая"
                Case.SHORT -> "май"
            }
        }
        6 -> {
            when (case) {
                Case.NONE -> "июнь"
                Case.OF -> "июня"
                Case.IN -> "июне"
                Case.SHORT -> "июн"
            }
        }
        7 -> {
            when (case) {
                Case.NONE -> "июль"
                Case.OF -> "июля"
                Case.IN -> "июле"
                Case.SHORT -> "июл"
            }
        }
        8 -> {
            when (case) {
                Case.NONE -> "август"
                Case.OF -> "августа"
                Case.IN -> "августе"
                Case.SHORT -> "авг"
            }
        }
        9 -> {
            when (case) {
                Case.NONE -> "сентябрь"
                Case.OF -> "сентября"
                Case.IN -> "сентябре"
                Case.SHORT -> "сен"
            }
        }
        10 -> {
            when (case) {
                Case.NONE -> "октябрь"
                Case.OF -> "октября"
                Case.IN -> "октябре"
                Case.SHORT -> "окт"
            }
        }
        11 -> {
            when (case) {
                Case.NONE -> "ноябрь"
                Case.OF -> "ноября"
                Case.IN -> "ноябре"
                Case.SHORT -> "ноя"
            }
        }
        12 -> {
            when (case) {
                Case.NONE -> "декабрь"
                Case.OF -> "декабря"
                Case.IN -> "декабре"
                Case.SHORT -> "дек"
            }
        }
        else -> ""
    }
}