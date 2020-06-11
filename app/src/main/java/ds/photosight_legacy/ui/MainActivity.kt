package ds.photosight_legacy.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.androidquery.util.AQUtility
import ds.photosight.R
import ds.photosight_legacy.*
import ds.photosight.utils.L
import ds.photosight_legacy.utils.Utils
import kotlinx.android.synthetic.main.main.*


class MainActivity : AppCompatActivity(), Constants, ViewPager.OnPageChangeListener, FragmentManager.OnBackStackChangedListener {

    private var mPagerAdapter: TabsPagerAdapter? = null
    private var mViewerFragment: ViewerFragment? = null
    private var isInViewer: Boolean = false                 // is viewer opened
    public var currentTab: Int = 0                     // currently selected list

    public var currPage: Int = 0
    private var mListSelects = IntArray(3)    // choises of each list


    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.main)


        if (b != null) {
            isInViewer = b.getBoolean("isInViewer", false)
            currentTab = b.getInt("tab")
            mListSelects = b.getIntArray("selects")
            currPage = b.getInt("page")
        } else {
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
        val bar = supportActionBar

        // tabs
        mPagerAdapter = TabsPagerAdapter(this)
        viewPager.adapter = mPagerAdapter
        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.setOnPageChangeListener(this)
        viewPager!!.currentItem = currentTab

        if (getApp().isPortrait()) {
            L.v("portrait main activity init. isInViewer?" + isInViewer)

            if (isInViewer) {
                //injectFragment(getViewerFragment(), R.id.main_layout, true);
                selectItem(mListSelects[currentTab])
                L.v("switched to viewer fragment")
            }
            toggleTabs(!isInViewer)


        } else {
            L.v("landscape main activity init")
            selectItem(mListSelects[currentTab])
            isInViewer = true
            toggleTabs(true)
        }


        showAboutIfNeed()
    }


    private fun getViewerFragment(): ViewerFragment? {
        val f = supportFragmentManager.findFragmentByTag(ViewerFragment::class.java.name) as ViewerFragment?
        return f
    }


    private fun injectFragment(what: Fragment, where: Int, backStack: Boolean) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(where, what, (what as Any).javaClass.getName())
        if (backStack)
            ft.addToBackStack(null)
        ft.commit()
        supportFragmentManager.executePendingTransactions()
    }

    fun selectItem(item: Int) {
        L.v("item selected $item")
        L.v("portrait? " + getApp().isPortrait())
        //
        mListSelects[currentTab] = item
        mViewerFragment = getViewerFragment()
        if (getApp().isPortrait()) {
            if (mViewerFragment == null) {
                L.v("viewer not found in the manager. creating new one")
                mViewerFragment = ViewerFragment()
                injectFragment(mViewerFragment!!, R.id.main_layout, true)
            } else {
                L.w("viewer found in manager. in portrait mode. weird!")

                supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(mViewerFragment!!).commit()
                supportFragmentManager.executePendingTransactions()
                injectFragment(mViewerFragment!!, R.id.main_layout, true)

            }
        } else {
            if (mViewerFragment == null) {
                L.v("viewer not found in the manager. creating new one")
                mViewerFragment = ViewerFragment()
                injectFragment(mViewerFragment!!, R.id.root, false)
            } else {
                L.v("viewer should be already in the place. need just invalidate it!")
                //setCurrPage(0);
                mViewerFragment!!.reload()
            }
        }
    }


    private fun setTitle() {
        L.v("setting default title")
        supportActionBar!!.setTitle(R.string.photosight)
        supportActionBar!!.setSubtitle(null)
    }


    fun setListSelection(list: Int, selection: Int) {
        mListSelects[list] = selection
    }


    fun getListSelection(list: Int): Int {
        return mListSelects[list]
    }


    fun getCurrentListSelection(): Int {
        return mListSelects[currentTab]
    }


    override fun onSaveInstanceState(b: Bundle) {
        super.onSaveInstanceState(b)
        b.putInt("tab", currentTab)
        b.putBoolean("isInViewer", isInViewer)
        b.putIntArray("selects", mListSelects)
        b.putInt("page", currPage)
    }


    private fun showAboutIfNeed() {

        val ver = try {
            val manager = packageManager.getPackageInfo(packageName, 0)
            manager.versionName
        } catch (e1: NameNotFoundException) {
            "100500"
        }

        if (App.prefs.getString("version", "0") != ver) {
            App.prefs.edit().putString("version", ver).apply()
            AQUtility.postDelayed({
                showAbout()
            }, 1000)

        }

    }

    override fun onPostResume() {
        super.onPostResume()
        invalidateOptionsMenu()
    }

    override fun onDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)

        super.onDestroy()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val c = supportFragmentManager.backStackEntryCount
        L.v("back stack amount:$c")
        if (App.prefs.getBoolean(Constants.PREFS_KEY_AUTO_CLEAR_CACHE, true) && c == 0) {
            L.d("clearing caches...")
            Utils.clearCaches(this)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i: Intent

        when (item.itemId) {

            R.id.im_settings -> {
                i = Intent(applicationContext, PreferencesActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.im_open_photosight -> {
                i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(Constants.URL_MAIN)
                startActivity(i)
            }
            R.id.im_about -> showAbout()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackStackChanged() {
        L.v("on back stack!")
        val c = supportFragmentManager.backStackEntryCount
        isInViewer = c != 0
        toggleTabs(!isInViewer)
    }


    private fun toggleTabs(enable: Boolean) {
        if (enable) {
            tabLayout.visibility = View.VISIBLE
            if (getApp().isPortrait())
                setTitle()
        } else {
            tabLayout.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //L.v("menu");
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        L.v("ON PAGE selected")
        currentTab = position
    }

    override fun onPageScrollStateChanged(state: Int) {
    }


    //
    // **********************************************************************************************************************************************
    // TabsPagerAdapter
    // **********************************************************************************************************************************************
    //
    class TabsPagerAdapter(a: FragmentActivity) : FragmentPagerAdapter(a.supportFragmentManager) {

        private val ctx: Context


        init {
            ctx = a
        }


        override fun getCount(): Int {
            return 2
        }


        override fun getItem(position: Int): Fragment = when (position) {
            0 -> Fragment.instantiate(ctx, CategoriesFragment::class.java.name, null)
            1 -> Fragment.instantiate(ctx, TopsFragment::class.java.name, null)
            else -> error("not supported")
        }


        override fun getPageTitle(position: Int): CharSequence = when (position) {
            0 -> ctx.getString(R.string.categories)
            1 -> ctx.getString(R.string.top)
            else -> super.getPageTitle(position) ?: "?"
        }
    }
}

