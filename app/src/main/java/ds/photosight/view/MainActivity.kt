package ds.photosight.view

import android.os.Bundle
import ds.photosight.viewmodel.MainViewModel
import ds.photosight.R
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    override val vm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar.title = getString(R.string.app_name)

        log.v("hello koin")
    }

}

