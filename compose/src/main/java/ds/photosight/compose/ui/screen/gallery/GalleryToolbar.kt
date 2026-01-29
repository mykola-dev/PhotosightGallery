package ds.photosight.compose.ui.screen.gallery

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ds.photosight.compose.data.getTitleId
import ds.photosight.compose.util.logCompositions
import ds.photosight.parser.CategoriesPhotosRequest

// import ds.photosight.parser.PhotosFilter

data class ToolbarState(
        val title: String,
        val subtitle: String?,
        val filter: PhotosFilter?,
        val onShowAboutDialog: () -> Unit,
        val onFilterSelected: (CategoriesPhotosRequest.FilterDumpCategory) -> Unit,
        val onSorterSelected: (CategoriesPhotosRequest.SortTypeCategory) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainToolbar(
        state: State<ToolbarState>,
        modifier: Modifier = Modifier,
) =
        with(state.value) {
            logCompositions("toolbar")
            TopAppBar(
                    title = {
                        Column(
                                modifier = Modifier.padding(horizontal = 16.dp).animateContentSize()
                        ) {
                            Text(title, style = MaterialTheme.typography.titleLarge)
                            if (subtitle != null) {
                                Text(subtitle, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    },
                    actions = {
                        if (filter != null) {
                            var filtersExpanded by remember { mutableStateOf(false) }
                            var sortersExpanded by remember { mutableStateOf(false) }
                            IconButton(onClick = { sortersExpanded = true }) {
                                Icon(Icons.Filled.Sort, null)
                                SortersMenu(
                                        sortersExpanded,
                                        filter.sortTypeCategory,
                                        { sortersExpanded = false }
                                ) {
                                    onSorterSelected(it)
                                    sortersExpanded = false
                                }
                            }
                            IconButton(onClick = { filtersExpanded = true }) {
                                Icon(Icons.Filled.FilterList, null)
                                FiltersMenu(
                                        filtersExpanded,
                                        filter.filterDumpCategory,
                                        { filtersExpanded = false }
                                ) {
                                    onFilterSelected(it)
                                    filtersExpanded = false
                                }
                            }
                        }
                        IconButton(onClick = onShowAboutDialog) { Icon(Icons.Filled.Info, null) }
                    },
                    modifier = modifier
            )
        }

@Composable
fun FiltersMenu(
        isExpanded: Boolean,
        selected: CategoriesPhotosRequest.FilterDumpCategory,
        onDismiss: () -> Unit,
        onSelected: (CategoriesPhotosRequest.FilterDumpCategory) -> Unit,
) {
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismiss) {
        CategoriesPhotosRequest.FilterDumpCategory.entries.forEach { filter ->
            DropdownMenuItem(
                    text = { Text(stringResource(filter.getTitleId())) },
                    onClick = { onSelected(filter) },
                    leadingIcon = { RadioButton(selected == filter, onClick = null) }
            )
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
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismiss) {
        CategoriesPhotosRequest.SortTypeCategory.entries.forEach { filter ->
            DropdownMenuItem(
                    text = { Text(stringResource(filter.getTitleId())) },
                    onClick = { onSelected(filter) },
                    leadingIcon = { RadioButton(selected == filter, onClick = null) }
            )
        }
    }
}

/*@Preview
@Composable
fun ToolbarPreview() {
    PhotosightTheme {
        MainToolbar(ToolbarState(title = "hello", subtitle = "world", PhotosFilter(), {}, {}, {}))
    }
}*/
