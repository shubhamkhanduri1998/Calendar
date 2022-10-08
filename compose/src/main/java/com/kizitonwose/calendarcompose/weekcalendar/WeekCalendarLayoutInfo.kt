package com.kizitonwose.calendarcompose.weekcalendar

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import com.kizitonwose.calendarcore.WeekDay

/**
 * Contains useful information about the currently displayed layout state of the calendar.
 * For example you can get the list of currently displayed months.
 *
 * Use [WeekCalendarState.layoutInfo] to retrieve this.
 *
 * @see LazyListLayoutInfo
 */
class WeekCalendarLayoutInfo(
    info: LazyListLayoutInfo,
    private val getIndexData: (Int) -> List<WeekDay>,
) : LazyListLayoutInfo by info {

    /**
     * The list of [WeekCalendarItemInfo] representing all the currently visible weeks.
     */
    val visibleWeeksInfo: List<WeekCalendarItemInfo>
        get() = visibleItemsInfo.map { info ->
            WeekCalendarItemInfo(info, getIndexData(info.index))
        }
}

/**
 * Contains useful information about an individual week on the calendar.
 *
 * @param dates The week in the list.

 * @see WeekCalendarLayoutInfo
 * @see LazyListItemInfo
 */
class WeekCalendarItemInfo(info: LazyListItemInfo, val dates: List<WeekDay>) :
    LazyListItemInfo by info
