package com.kizitonwose.calendarcompose

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import com.kizitonwose.calendarcore.CalendarMonth

/**
 * Contains useful information about the currently displayed layout state of the calendar.
 * For example you can get the list of currently displayed months.
 *
 * Use [CalendarState.layoutInfo] to retrieve this.
 * @see LazyListLayoutInfo
 */
class CalendarLayoutInfo(info: LazyListLayoutInfo, private val month: (Int) -> CalendarMonth) :
    LazyListLayoutInfo by info {

    /**
     * The list of [CalendarItemInfo] representing all the currently visible months.
     */
    val visibleMonthsInfo: List<CalendarItemInfo>
        get() = visibleItemsInfo.map {
            CalendarItemInfo(it, month(it.index))
        }
}

/**
 * Contains useful information about an individual [CalendarMonth] on the calendar.
 *
 * @param month The month in the list.
 *
 * @see CalendarLayoutInfo
 * @see LazyListItemInfo
 */
class CalendarItemInfo(info: LazyListItemInfo, val month: CalendarMonth) : LazyListItemInfo by info


internal val CalendarLayoutInfo.completelyVisibleMonths: List<CalendarMonth>
    get() {
        val visibleItemsInfo = this.visibleMonthsInfo.toMutableList()
        return if (visibleItemsInfo.isEmpty()) {
            emptyList()
        } else {
            val lastItem = visibleItemsInfo.last()
            val viewportSize = this.viewportEndOffset + this.viewportStartOffset
            if (lastItem.offset + lastItem.size > viewportSize) {
                visibleItemsInfo.removeLast()
            }
            val firstItem = visibleItemsInfo.firstOrNull()
            if (firstItem != null && firstItem.offset < this.viewportStartOffset) {
                visibleItemsInfo.removeFirst()
            }
            visibleItemsInfo.map { it.month }
        }
    }
