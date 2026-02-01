@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package ds.photosight.shared.ui.screen.gallery

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ds.photosight.shared.ui.theme.Palette
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.categories
import photosight.shared.generated.resources.ratings

@Composable
fun BottomMenu(
    shitState: SheetState,
    menuState: MenuState,
    onMenuItemSelected: (MenuItemState) -> Unit,
) {

    LaunchedEffect(menuState) {
        if (shitState.currentValue != menuState.bottomSheetState && shitState.hasExpandedState) {
            shitState.partialExpand()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        val pagerState = rememberPagerState(pageCount = { MenuTabs.entries.size }, initialPage = 0)
        val tabIndex = pagerState.currentPage

        val screenHeightPx = LocalWindowInfo.current.containerSize.height.toFloat()

        // Stabilized calculation using progress and state values
        val collapsedFraction by remember {
            derivedStateOf {
                try {
                    val offset = shitState.requireOffset()
                    (offset / (screenHeightPx / 1.5f)).coerceIn(0f, 1f)
                } catch (e: Exception) {
                    when (shitState.currentValue) {
                        SheetValue.PartiallyExpanded -> 1f
                        SheetValue.Expanded -> 0f
                        else -> 0f
                    }
                }
            }
        }
        val sbHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val nbHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val tabsPadding = remember(collapsedFraction) { sbHeight * (1 - collapsedFraction) }

        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Palette.greyDark,
            modifier = Modifier.padding(top = tabsPadding),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                    color = Palette.greyDark
                )
            }
        ) {
            val coroutineScope = rememberCoroutineScope()
            MenuTabs.entries.forEach { item ->
                val tabTitle = when(item) {
                    MenuTabs.RATINGS -> stringResource(Res.string.ratings)
                    MenuTabs.CATEGORIES -> stringResource(Res.string.categories)
                }
                Tab(
                    selected = tabIndex == item.ordinal,
                    onClick = {
                        coroutineScope.launch {
                            if (shitState.currentValue == SheetValue.PartiallyExpanded) {
                                pagerState.scrollToPage(item.ordinal)
                                shitState.expand()
                            } else {
                                pagerState.animateScrollToPage(item.ordinal)
                            }
                        }
                    },
                    text = { Text(tabTitle.uppercase()) }
                )
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
                    val currTab = MenuTabs.entries[page]
                    val menuItems =
                        when (currTab) {
                            MenuTabs.RATINGS -> menuState.ratings
                            MenuTabs.CATEGORIES -> menuState.categories
                        }
                    items(menuItems) {
                        MenuItem(
                            model = it,
                            isSelected = it == menuState.selectedItem,
                            onMenuItemSelected = onMenuItemSelected
                        )
                    }
                },
            )
        }
    }
}

@Composable
fun MenuItem(
    model: MenuItemState,
    isSelected: Boolean,
    onMenuItemSelected: (MenuItemState) -> Unit
) {
    val transition = updateTransition(isSelected, "selector")

    val bgColor by
    transition.animateColor({ tween(if (targetState) 0 else 500) }, "bg color") { selected
        ->
        if (selected) Palette.greyDark else Color.Transparent
    }
    val textColor = if (isSelected) Palette.primary else Palette.surface

    val style = MaterialTheme.typography.titleMedium

    val title = when (model) {
        is CategoryMenuItemState -> stringResource(model.category.titleRes)
        is RatingMenuItemState -> stringResource(model.type.titleRes)
    }

    Text(
        text = title,
        color = textColor,
        style = style,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .background(bgColor)
                .clickable { onMenuItemSelected(model) }
                .padding(16.dp)
                .fillMaxSize()
    )
}
