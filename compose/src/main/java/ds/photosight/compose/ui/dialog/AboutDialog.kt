package ds.photosight.compose.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ds.photosight.compose.BuildConfig
import ds.photosight.compose.R
import ds.photosight.compose.ui.theme.PhotosightTheme
import ds.photosight.compose.ui.widget.LinkifyText

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val appVersion = BuildConfig.VERSION_NAME

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.about_title_) + appVersion,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LinkifyText(
                    linkColor = MaterialTheme.colors.secondary,
                    text = stringResource(
                        R.string.abouttext,
                        stringResource(R.string.app_changelog),
                        stringResource(id = R.string.copyright)
                    ),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f),

                    )

                OutlinedButton(onClick = onDismiss, Modifier.align(Alignment.End)) {
                    Text(stringResource(android.R.string.ok))
                }

            }
        }
    }
}

@Preview
@Composable
fun AboutDialogPreview() {
    PhotosightTheme {
        AboutDialog {

        }
    }
}