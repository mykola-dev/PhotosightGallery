package ds.photosight.compose.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import ds.photosight.compose.BuildConfig

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun logCompositions(msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(1) }
        SideEffect { ref.value++ }
        Log.d("RECOMPOSED", "==> $msg (${ref.value} times)")
    }
}