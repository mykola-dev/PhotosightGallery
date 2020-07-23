package ds.photosight.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ds.photosight.utils.snack
import ds.photosight.ui.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    protected abstract val vm: BaseViewModel

    override val coroutineContext: CoroutineContext = lifecycleScope.coroutineContext

    @Inject
    protected lateinit var log: Timber.Tree

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.v("${this::class.simpleName} onCreate")

        vm.showSnackbarCommand.observe(this) {
            root.snack(it)
        }
    }
}
