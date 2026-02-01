package ds.photosight.shared.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ds.photosight.shared.core.widget.LinkifyText
import org.jetbrains.compose.resources.stringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.about_title
import photosight.shared.generated.resources.abouttext
import photosight.shared.generated.resources.app_changelog
import photosight.shared.generated.resources.copyright
import photosight.shared.generated.resources.ok

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxHeight(0.9f)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(Res.string.about_title),
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                LinkifyText(
                    linkColor = MaterialTheme.colorScheme.secondary,
                    text = stringResource(
                        Res.string.abouttext,
                        stringResource(Res.string.app_changelog),
                        stringResource(Res.string.copyright)
                    ),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f),
                )

                OutlinedButton(onClick = onDismiss, Modifier.align(Alignment.End)) {
                    Text(stringResource(Res.string.ok))
                }
            }
        }
    }
}
