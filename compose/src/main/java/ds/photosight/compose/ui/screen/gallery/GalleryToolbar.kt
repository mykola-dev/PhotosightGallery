package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ds.photosight.compose.data.getTitleId
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.parser.CategoriesPhotosRequest

data class ToolbarState(
    val title: String,
    val subtitle: String?,
    val filter: PhotosFilter?,
    val onShowAboutDialog: () -> Unit,
    val onFilterSelected: (CategoriesPhotosRequest.FilterDumpCategory) -> Unit,
    val onSorterSelected: (CategoriesPhotosRequest.SortTypeCategory) -> Unit,
)

@Composable
fun MainToolbar(
    state: ToolbarState,
    modifier: Modifier = Modifier,
) = with(state) {
    TopAppBar(
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
                    .animateContentSize()
            ) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 12.sp)
                }
            }
            if (filter != null) {
                var filtersExpanded by remember { mutableStateOf(false) }
                var sortersExpanded by remember { mutableStateOf(false) }
                IconButton(onClick = { sortersExpanded = true }) {
                    Icon(Icons.Filled.Sort, null)
                    SortersMenu(sortersExpanded, filter.sortTypeCategory, { sortersExpanded = false }) {
                        onSorterSelected(it)
                        sortersExpanded = false
                    }
                }
                IconButton(onClick = { filtersExpanded = true }) {
                    Icon(Icons.Filled.FilterList, null)
                    FiltersMenu(filtersExpanded, filter.filterDumpCategory, { filtersExpanded = false }) {
                        onFilterSelected(it)
                        filtersExpanded = false
                    }
                }
            }
            IconButton(onClick = onShowAboutDialog) {
                Icon(Icons.Filled.Info, null)
            }
        }
    }
}

@Composable
fun FiltersMenu(
    isExpanded: Boolean,
    selected: CategoriesPhotosRequest.FilterDumpCategory,
    onDismiss: () -> Unit,
    onSelected: (CategoriesPhotosRequest.FilterDumpCategory) -> Unit,
) {
    DropdownMenu(isExpanded, onDismiss) {
        CategoriesPhotosRequest.FilterDumpCategory.values().forEach { filter ->
            DropdownMenuItem({ onSelected(filter) }) {
                RadioButton(selected == filter, { onSelected(filter) })
                Text(stringResource(filter.getTitleId()))
            }
        }
    }
}

@Composable
fun SortersMenu(
    isExpanded: Boolean,
    selected: CategoriesPhotosRequest.SortTypeCategory,
    onDismiss: () -> Unit,
    onSelected: (CategoriesPhotosRequest.SortTypeCategory) -> Unit
) {
    DropdownMenu(isExpanded, onDismiss) {
        CategoriesPhotosRequest.SortTypeCategory.values().forEach { filter ->
            DropdownMenuItem({ onSelected(filter) }) {
                RadioButton(selected == filter, { onSelected(filter) })
                Text(stringResource(filter.getTitleId()))
            }
        }
    }
}

@Preview
@Composable
fun ToolbarPreview() {
    PhotosightTheme {
        MainToolbar(ToolbarState(title = "hello", subtitle = "world", PhotosFilter(), {}, {}, {}))
    }
}

