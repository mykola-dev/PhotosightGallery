package ds.photosight.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.androidquery.util.AQUtility
import com.astuetz.PagerSlidingTabStrip
import com.flurry.android.FlurryAgent
import ds.photosight.*
import ds.photosight.utils.L
import ds.photosight.utils.Utils
import kotlin.properties.Delegates


public class MainActivity : AppCompatActivity(), Constants, ViewPager.OnPageChangeListener, android.support.v4.app.FragmentManager.OnBackStackChangedListener {

    private var mViewPager: ViewPager? = null
    private var mPagerAdapter: TabsPagerAdapter? = null
    private var mViewerFragment: ViewerFragment? = null
    private var isInViewer: Boolean = false                 // is viewer opened
    public var currentTab: Int = 0                     // currently selected list
    private var tabs: PagerSlidingTabStrip by Delegates.notNull()


    public var currPage: Int = 0
    private var mListSelects = IntArray(3)    // choises of each list


    override fun onCreate(b: Bundle?) {
        super<AppCompatActivity>.onCreate(b)
        setContentView(R.layout.main)


        if (b != null) {
            isInViewer = b.getBoolean("isInViewer", false)
            currentTab = b.getInt("tab")
            mListSelects = b.getIntArray("selects")
            currPage = b.getInt("page")
        } else {
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this)
        val bar = getSupportActionBar()

        // tabs
        mViewPager = findViewById(R.id.viewPager) as ViewPager
        mPagerAdapter = TabsPagerAdapter(this)
        mViewPager!!.setAdapter(mPagerAdapter)
        tabs = findViewById(R.id.navigation) as PagerSlidingTabStrip
        tabs.setViewPager(mViewPager)
        tabs.setOnPageChangeListener(this)
        //tabs.setTextColorResource(R.color.tabs);
        mViewPager!!.setCurrentItem(currentTab)

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


    public fun getViewerFragment(): ViewerFragment? {
        val f = getSupportFragmentManager().findFragmentByTag(javaClass<ViewerFragment>().getName()) as ViewerFragment?
        //if (f == null)
        //f = new ViewerFragment();

        return f
    }


    public fun injectFragment(what: Fragment?, where: Int, backStack: Boolean) {
        val ft = getSupportFragmentManager().beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(where, what, (what as Any).javaClass.getName())
        if (backStack)
            ft.addToBackStack(null)
        ft.commit()
        getSupportFragmentManager().executePendingTransactions()
    }


    private fun addTabs() {

    }


    public fun selectItem(item: Int) {
        L.v("item selected " + item)
        L.v("portrait? " + getApp().isPortrait())
        //
        mListSelects[currentTab] = item
        mViewerFragment = getViewerFragment()
        if (getApp().isPortrait()) {
            if (mViewerFragment == null) {
                L.v("viewer not found in the manager. creating new one")
                mViewerFragment = ViewerFragment()
                injectFragment(mViewerFragment, R.id.main_layout, true)
            } else {
                L.w("viewer found in manager. in portrait mode. weird!")

                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(mViewerFragment).commit()
                getSupportFragmentManager().executePendingTransactions()
                injectFragment(mViewerFragment, R.id.main_layout, true)

            }
        } else {
            if (mViewerFragment == null) {
                L.v("viewer not found in the manager. creating new one")
                mViewerFragment = ViewerFragment()
                injectFragment(mViewerFragment, R.id.viewer_frame, false)
            } else {
                L.v("viewer should be already in the place. need just invalidate it!")
                //setCurrPage(0);
                mViewerFragment!!.reload()
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        //getSupportFragmentManager().popBackStackImmediate();
        super<AppCompatActivity>.onConfigurationChanged(newConfig)
    }


    private fun setTitle() {
        L.v("setting default title")
        getSupportActionBar()!!.setTitle(R.string.photosight)
        getSupportActionBar()!!.setSubtitle(null)
    }


    public fun setListSelection(list: Int, selection: Int) {
        mListSelects[list] = selection
    }


    public fun getListSelection(list: Int): Int {
        return mListSelects[list]
    }


    public fun getCurrentListSelection(): Int {
        return mListSelects[currentTab]
    }


    override fun onSaveInstanceState(b: Bundle) {
        //super.onSaveInstanceState(b);
        b.putInt("tab", currentTab)
        b.putBoolean("isInViewer", isInViewer)
        b.putIntArray("selects", mListSelects)
        b.putInt("page", currPage)
    }


    private fun showAboutIfNeed() {

        val ver: String
        try {
            val manager = getPackageManager().getPackageInfo(getPackageName(), 0)
            ver = manager.versionName
        } catch (e1: NameNotFoundException) {
            ver = "100500"
        }

        if (!App._prefs.getString("version", "0").equals(ver)) {
            App._prefs.edit().putString("version", ver).commit()
            AQUtility.postDelayed({
                showAbout()
            }, 1000)

        }

    }


    override fun onStart() {
        super<AppCompatActivity>.onStart()
        FlurryAgent.onStartSession(this, Constants.FLURRY)
    }


    override fun onPostResume() {
        super<AppCompatActivity>.onPostResume()
        invalidateOptionsMenu()
    }


    override fun onStop() {
        super<AppCompatActivity>.onStop()
        FlurryAgent.onEndSession(this)

    }


    override fun onDestroy() {
        /*if (ad != null)
			ad.destroy();*/

        getSupportFragmentManager().removeOnBackStackChangedListener(this)

        super<AppCompatActivity>.onDestroy()
    }


    override fun onBackPressed() {
        val c = getSupportFragmentManager().getBackStackEntryCount()
        L.v("back stack amount:" + c)
        if (App._prefs.getBoolean(Constants.PREFS_KEY_AUTO_CLEAR_CACHE, true) && c == 0) {
            L.d("clearing caches...")
            Utils.clearCaches(this)
        }

        super<AppCompatActivity>.onBackPressed()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val i: Intent

        when (item!!.getItemId()) {

            R.id.im_settings -> {
                i = Intent(getApplicationContext(), javaClass<PreferencesActivity>())
                startActivity(i)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.im_open_photosight -> {
                i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(Constants.URL_MAIN))
                startActivity(i)
            }
            R.id.im_about -> showAbout()
        }
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }


    override fun onBackStackChanged() {
        L.v("on back stack!")
        val c = getSupportFragmentManager().getBackStackEntryCount()
        isInViewer = c != 0
        toggleTabs(!isInViewer)
    }


    public fun toggleTabs(enable: Boolean) {
        if (enable) {
            tabs.setVisibility(View.VISIBLE)
            if (getApp().isPortrait())
                setTitle()
        } else
            tabs.setVisibility(View.GONE)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //L.v("menu");
        getMenuInflater().inflate(R.menu.main_menu, menu)
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
    public class TabsPagerAdapter(a: FragmentActivity) : FragmentPagerAdapter(a.getSupportFragmentManager()) {

        private val ctx: Context


        init {
            ctx = a
        }


        override fun getCount(): Int {
            return 2
        }


        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return Fragment.instantiate(ctx, javaClass<CategoriesFragment>().getName(), null)
                1 -> return Fragment.instantiate(ctx, javaClass<TopsFragment>().getName(), null)
                else -> return null
            }
        }


        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return ctx.getString(R.string.categories)
                1 -> return ctx.getString(R.string.top)
                else -> return super.getPageTitle(position)
            }
        }
    }
}

