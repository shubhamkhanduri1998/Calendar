package com.kizitonwose.calendarsample.compose

import android.os.Build
import android.view.View
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendarcompose.CalendarState
import com.kizitonwose.calendarcompose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendarcore.WeekDay
import com.kizitonwose.calendarsample.R
import com.kizitonwose.calendarsample.findActivity
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import java.time.YearMonth

fun Modifier.clickable(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) LocalIndication.current else null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick
    )
}

@Composable
fun StatusBarColorUpdateEffect(color: Color, isLight: Boolean = false) {
    val activity = LocalContext.current.findActivity()
    val defaultStatusBarColor = colorResource(R.color.colorPrimaryDark)
    DisposableEffect(LocalLifecycleOwner.current) {
        activity.window.apply {
            statusBarColor = color.toArgb()
            if (isLight && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
        onDispose {
            activity.window.apply {
                statusBarColor = defaultStatusBarColor.toArgb()
                if (isLight) decorView.systemUiVisibility = 0
            }
        }
    }
}

@Composable
fun NavigationIcon(onBackClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .padding(8.dp)
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onBackClick)) {
        Icon(
            tint = Color.White,
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back"
        )
    }
}

/**
 * Returns the first fully visible month in the layout.
 *
 * @see [rememberFirstVisibleMonthAfterScroll]
 */
@Composable
fun rememberFirstCompletelyVisibleMonthNonNull(state: CalendarState): YearMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth.yearMonth) }
    // Only take non-null values as null will be produced when the
    // list is mid-scroll as no index will be completely visible.
    LaunchedEffect(state) {
        snapshotFlow { state.firstCompletelyVisibleMonth?.yearMonth }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

/**
 * Alternative way to find first visible month in a paged calendar **after** scrolling stops.
 *
 * @see [rememberFirstCompletelyVisibleMonthNonNull]
 * @see [rememberFirstVisibleWeekAfterScroll]
 */
@Composable
fun rememberFirstVisibleMonthAfterScroll(state: CalendarState): YearMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth.yearMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleMonth.value = state.firstVisibleMonth.yearMonth }
    }
    return visibleMonth.value
}

/**
 * Find first visible week in a paged week calendar **after** scrolling stops.
 */
@Composable
fun rememberFirstVisibleWeekAfterScroll(state: WeekCalendarState): List<WeekDay> {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleWeek.value = state.firstVisibleWeek }
    }
    return visibleWeek.value
}
