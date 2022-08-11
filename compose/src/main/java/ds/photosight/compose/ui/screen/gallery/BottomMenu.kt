package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.compose.util.log
import kotlinx.coroutines.launch

@Composable
fun BottomMenu(
    shitState: BottomSheetState,
    menuState: MenuState,
    onMenuItemSelected: (MenuItemState) -> Unit,
) {

    LaunchedEffect(menuState) {
        log.w("bss effect: ${menuState.bottomSheetState}")
        if (shitState.currentValue != menuState.bottomSheetState && shitState.isExpanded) {
            shitState.collapse()
        }
    }

    Column {
        val pagerState = rememberPagerState(initialPage = 0)
        val tabIndex = pagerState.currentPage

        val collapsedFraction by derivedStateOf {
            if (shitState.progress.to == BottomSheetValue.Collapsed) {
                1 - shitState.progress.fraction
            } else {
                shitState.progress.fraction
            }
        }
        val sbHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val nbHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val tabsPadding = remember(collapsedFraction) { sbHeight * collapsedFraction }

        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.surface,
            modifier = Modifier.padding(top = tabsPadding)
        ) {
            val coroutineScope = rememberCoroutineScope()
            MenuTabs.values().forEach { item ->
                Tab(
                    selected = false,   // this isn't relevant
                    onClick = {
                        coroutineScope.launch {
                            if (shitState.isCollapsed) {
                                pagerState.scrollToPage(item.ordinal)
                                shitState.expand()
                            } else {
                                pagerState.animateScrollToPage(item.ordinal)
                            }
                        }
                    },
                    text = { Text(stringResource(item.resId).uppercase()) })
            }
        }
        val pagerPadding = remember(collapsedFraction) { nbHeight * (1 - collapsedFraction) }
        Spacer(modifier = Modifier.height(pagerPadding))

        HorizontalPager(
            count = MenuTabs.values().size,
            state = pagerState,
            //modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.Top,
        ) { index ->
            LazyColumn(content = {
                val currTab = MenuTabs.values()[index]
                val menuItems = when (currTab) {
                    MenuTabs.RATINGS -> menuState.ratings
                    MenuTabs.CATEGORIES -> menuState.categories
                }
                items(menuItems) {
                    MenuItem(model = it, isSelected = it == menuState.selectedItem, onMenuItemSelected = onMenuItemSelected)
                }
            })
        }
    }
}

@Composable
fun MenuItem(model: MenuItemState, isSelected: Boolean, onMenuItemSelected: (MenuItemState) -> Unit) {
    val transition = updateTransition(isSelected, "selector")

    val bgColor by transition.animateColor({ tween(if (targetState) 0 else 500) }, "bg color") { selected ->
        if (selected) Palette.surface else Color.Transparent
    }
    val textColor = if (isSelected) Palette.primary else Palette.surface

    val style = MaterialTheme.typography.subtitle1

    Text(text = model.title,
        color = textColor,
        style = style,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(bgColor)
            .clickable { onMenuItemSelected(model) }
            .padding(16.dp)
            .fillMaxSize())
}

@Preview()
@Composable
fun MenuItemPreview() {
    PhotosightTheme {
        Column(Modifier.background(Palette.primary)) {
            MenuItem(CategoryMenuItemState(1, "Selected!"), true, {})
            MenuItem(CategoryMenuItemState(1, "item"), false, {})
        }
    }
}

@Preview
@Composable
fun BottomMenuPreview() {
    PhotosightTheme {
        BottomMenu(BottomSheetState(BottomSheetValue.Expanded), MenuState(), {})
    }
}