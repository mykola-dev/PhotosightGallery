package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ds.photosight.compose.ui.theme.Palette
import ds.photosight.compose.ui.theme.PhotosightTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomMenu(
    shitState: BottomSheetState,
    menuState: MenuState,
    onMenuItemSelected: (MenuItemState) -> Unit,
) {

    LaunchedEffect(menuState) {
        if (shitState.currentValue != menuState.bottomSheetState && shitState.isExpanded) {
            shitState.collapse()
        }
    }

    Column(Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { MenuTabs.values().size }, initialPage = 0)
        val tabIndex = pagerState.currentPage

        // Stabilized calculation using progress and state values
        val collapsedFraction by remember {
            derivedStateOf {
                when (shitState.currentValue) {
                    BottomSheetValue.Collapsed -> {
                        if (shitState.targetValue == BottomSheetValue.Collapsed) 1f
                        else 1f - shitState.progress
                    }
                    BottomSheetValue.Expanded -> {
                        if (shitState.targetValue == BottomSheetValue.Expanded) 0f
                        else shitState.progress
                    }
                    else -> 0f
                }
            }
        }
        val sbHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val nbHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val tabsPadding = remember(collapsedFraction) { sbHeight * (1 - collapsedFraction) }

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
        val pagerPadding = remember(collapsedFraction, nbHeight) { nbHeight * collapsedFraction }
        Spacer(modifier = Modifier.height(pagerPadding))

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.weight(1f)
        ) { page ->
            LazyColumn(
                contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                content = {
                    val currTab = MenuTabs.values()[page]
                    val menuItems = when (currTab) {
                        MenuTabs.RATINGS -> menuState.ratings
                        MenuTabs.CATEGORIES -> menuState.categories
                    }
                    items(menuItems) {
                        MenuItem(model = it, isSelected = it == menuState.selectedItem, onMenuItemSelected = onMenuItemSelected)
                    }
                },
            )
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

    Text(
        text = model.title,
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
    val density = LocalDensity.current
    PhotosightTheme {
        BottomMenu(BottomSheetState(BottomSheetValue.Expanded, density), MenuState(), {})
    }
}