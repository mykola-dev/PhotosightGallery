package ds.photosight.parser

import java.util.*
import java.util.concurrent.TimeUnit

interface Multipage {
    val page: Page
}

interface Page {
    val index: Int
    val key: String
}

class SimplePage(override val index: Int) : Page {
    init {
        if (index < 1) error("currentPage must be > 0")
    }
    override val key: String = index.toString()
}

class DatePage(private val calendar: Calendar) : Page {
    init {
        if (index < 1) error("currentPage must be > 0")
    }

    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH) + 1
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

    override val index: Int
        get() {
            val start: Long = calendar.timeInMillis
            val end: Long = now.timeInMillis
            return TimeUnit.MILLISECONDS.toDays(end - start).toInt() + 1
        }
    override val key: String = "$year/$month/$day"

    companion object {
        fun fromPage(page: Int): DatePage {
            val date = now
            date.add(Calendar.DAY_OF_YEAR, 1 - page)
            return DatePage(date)
        }

        fun fromDate(year: Int, month: Int, day: Int): DatePage {
            val date = Calendar.getInstance()
            date.set(year, month - 1, day)
            return DatePage(date)
        }

        val now: Calendar get() = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
    }
}
