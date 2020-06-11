package ds.photosight.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ds.photosight.utils.snack
import ds.photosight.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    protected abstract val vm: BaseViewModel

    override val coroutineContext: CoroutineContext = lifecycleScope.coroutineContext

    protected val log: Timber.Tree by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.v("${this::class.simpleName} onCreate")

        vm.showSnackbarCommand.observe(this) {
            root.snack(it)
        }
    }
}
