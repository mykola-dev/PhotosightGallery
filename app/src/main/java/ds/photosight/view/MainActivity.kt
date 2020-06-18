package ds.photosight.view

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ds.photosight.viewmodel.MainViewModel
import ds.photosight.R
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar.title = getString(R.string.app_name)

        log.v("hello hilt!")
    }

}

