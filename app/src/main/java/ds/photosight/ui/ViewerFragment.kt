package ds.photosight.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.ImageView.ScaleType
import com.androidquery.AQuery
import com.androidquery.util.AQUtility
import de.greenrobot.event.EventBus
import ds.photosight.App
import ds.photosight.Constants
import ds.photosight.R
import ds.photosight.R.anim
import ds.photosight.R.array
import ds.photosight.R.id
import ds.photosight.event.PhotoInfo
import ds.photosight.model.ViewerData
import ds.photosight.model.ViewerData.OnLoadListener
import ds.photosight.utils.L
import java.util.ArrayList
import kotlin.properties.Delegates

public class ViewerFragment : Fragment(), Constants, ViewPager.OnPageChangeListener {
    private val app: App? = null
    private var grid: GridView by Delegates.notNull()
    private var progress: View? = null
    private var currPage = 0
    private var currPhoto = 0
    private var viewerData: ViewerData? = null
    private var pageAdapter: ViewerPagerAdapter? = null
    private var viewPager: ViewPager? = null
    private var mColumnWidth: Int = 0
    private var aq: AQuery? = null
    private var tempdata: List<Map<Int, String>>? = null


    override fun onCreate(b: Bundle?) {

        super<Fragment>.onCreate(b)

    }


    private fun getRoot(): MainActivity {
        return getActivity() as MainActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        L.v("viewer onActivity created")
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<Fragment>.onViewCreated(view, savedInstanceState)
        L.v("Viewer view created!")

        aq = AQuery(getActivity())
        currPage = getRoot().currPage

        /*if (App.getInstance().isPortrait())
			getRoot().toggleTabs(false);*/

        // setup title
        setTitle()

        setHasOptionsMenu(true)

        pageAdapter = ViewerPagerAdapter(getActivity(), getNumOfPages())
        viewPager!!.setAdapter(pageAdapter)
        viewPager!!.setOnPageChangeListener(this)
        viewPager!!.setCurrentItem(currPage, false)
        //if (currPage == 0) {
        AQUtility.postDelayed(object : Runnable {
            override fun run() {
                initViews(getCurrentPage(), false)
            }
        }, 200)
        //}

    }


    override fun onResume() {
        super<Fragment>.onResume()
        L.v("viewer onresume")
        val info = EventBus.getDefault().getStickyEvent<PhotoInfo>(javaClass<PhotoInfo>()) as PhotoInfo?
        if (info != null) {
            //T.show(getActivity(), "got some event!");
            getRoot().currentTab = info.tab
            getRoot().currPage = info.page
            currPage = info.page
            currPhoto = info.item
            L.v("curr photo: " + currPhoto)
            tempdata = info.data
            if (tempdata != null) {
                viewPager!!.setCurrentItem(currPage, false)
                AQUtility.postDelayed(object : Runnable {
                    override fun run() {
                        initViews(getCurrentPage(), false)
                    }
                }, 200)
            }

            EventBus.getDefault().removeStickyEvent(info)
        }
    }


    private fun getNumOfPages(): Int {
        if (getRoot().currentTab == Constants.TAB_TOPS && getRoot().getListSelection(Constants.TAB_TOPS) != Constants.ITEM_TOP_DAY)
            return 1
        else
            return 100500
    }


    private fun setTitle() {
        val ab = getRoot().getSupportActionBar()
        L.v("setting 2 line title")
        ab.setDisplayShowCustomEnabled(false)
        ab.setSubtitle(getResources().getString(R.string.page_) + (currPage + 1))
        val t: String?
        when (getRoot().currentTab) {
            Constants.TAB_CATEGORIES -> t = getResources().getStringArray(array.categories_array)[getRoot().getCurrentListSelection()]
            Constants.TAB_TOPS -> t = getString(R.string.top_in) + getResources().getStringArray(array.tops_array)[getRoot().getCurrentListSelection()]
            else -> t = null
        }
        ab.setTitle(t!!.toUpperCase())
    }


    public fun reload() {
        tempdata = null
        pageAdapter = ViewerPagerAdapter(getActivity(), getNumOfPages())
        viewPager!!.setAdapter(pageAdapter)
        viewPager!!.setOnPageChangeListener(this)
        currPage = getRoot().currPage
        viewPager!!.setCurrentItem(currPage, false)
        initViews(getCurrentPage(), false)

    }


    private fun initViews(v: View, forceUpdate: Boolean) {
        setTitle()
        grid = v.findViewById(id.viewerGrid) as GridView
        grid.setSelector(R.drawable.list_item_background)
        grid.setDrawSelectorOnTop(true)
        val gridWidth = grid.getWidth()
        L.v("grid width=$gridWidth")
        if (gridWidth!=0) {
            val numOfColumns = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Constants.PREFS_KEY_COLUMNS, "2"))!!
            mColumnWidth = gridWidth / numOfColumns
            grid.setColumnWidth(mColumnWidth)
            grid.setNumColumns(numOfColumns)
        }

        if (grid.getAdapter() != null && !forceUpdate) {
            L.w("grid.getAdapter() != null && !forceUpdate")
            if (currPhoto != 0) {
                grid.setSelection(currPhoto)
                //currPhoto = 0;
            }
            return
        }
        progress = v.findViewById(id.viewerLoader)

        if (tempdata != null) {
            progress!!.setVisibility(View.GONE)
            loadThumbs(tempdata)
            tempdata = null
            return
        }

        progress!!.setVisibility(View.VISIBLE)
        grid.setVisibility(View.GONE)
        val tab = getRoot().currentTab
        viewerData = ViewerData(tab, getRoot().getListSelection(tab), currPage)
        viewerData!!.setOnLoadListener(object : OnLoadListener {

            override fun onLoad(result: ArrayList<Map<Int, String>>, page: Int) {
                if (getActivity() == null)
                    return

                if (page != currPage)
                    return

                if (progress != null && getActivity() != null) {
                    val a = AnimationUtils.loadAnimation(getActivity(), anim.fade_out)
                    a.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {
                        }


                        override fun onAnimationEnd(animation: Animation) {
                            progress!!.clearAnimation()
                            progress!!.setVisibility(View.GONE)
                        }


                        override fun onAnimationRepeat(animation: Animation) {
                        }
                    })
                    progress!!.startAnimation(a)
                }

                loadThumbs(result)

            }


            override fun onProgress(page: Int, s: Int) {
                if (page != currPage)
                    return
                //progress.setProgress(s);
            }


            override fun onProgressEnd(page: Int) {
                if (page != currPage)
                    return

            }
        })

    }


    private fun loadThumbs(result: List<Map<Int, String>>?) {
        val data = result
        //progress.setVisibility(View.GONE);
        if (data == null) {
            if (getRoot().currentTab == Constants.TAB_TOPS && getRoot().getListSelection(Constants.TAB_TOPS) == Constants.ITEM_TOP_DAY)
                Toast.makeText(getActivity(), R.string.no_photos_try_next_page_, 1).show()
            else
                Toast.makeText(getActivity(), R.string.connection_error, 0).show()
            return
        }
        grid.setVisibility(View.VISIBLE)
        val adapter = GridAdapter(getActivity(), result, mColumnWidth)
        grid.setAdapter(adapter)
        if (currPhoto != 0) {
            L.v("selecting grid cell")
            grid.setSelection(currPhoto)
            currPhoto = 0
        }
        grid.setOnItemClickListener(object : AdapterView.OnItemClickListener {

            override fun onItemClick(arg0: AdapterView<*>, arg1: View, pos: Int, arg3: Long) {
                val i = Intent(getActivity(), javaClass<GalleryActivity>())
                // Bundle e;
                i.putExtra("data", data as ArrayList<Any>)
                i.putExtra("item", pos)
                i.putExtra("page", currPage)
                i.putExtra("tab", getRoot().currentTab)
                i.putExtra("category", getRoot().getCurrentListSelection())
                Log.d("startGallery", "item" + pos + " page" + currPage)
                startActivity(i)
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        })

        registerForContextMenu(grid)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewPager = ViewPager(getActivity())
        viewPager!!.setId(VIEWPAGER_ID)
        return viewPager
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        // if (menu == null)
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu)

        super<Fragment>.onCreateContextMenu(menu, v, menuInfo)
    }


    override fun onContextItemSelected(item: android.view.MenuItem?): Boolean {
        val pos = (item!!.getMenuInfo() as AdapterView.AdapterContextMenuInfo).position
        when (item.getItemId()) {
            R.id.imc_open_in_browser -> showInBrowser(pos)
            R.id.imc_show_detailed_info -> showInfo(pos)
        }// Toast.makeText(getActivity(), String.valueOf(Runtime.getRuntime().maxMemory()/1024/1024),1).show();
        //
        return super<Fragment>.onContextItemSelected(item)
    }


    private fun showInfo(pos: Int) {
        val a = grid.getAdapter() as GridAdapter
        val newFragment = InfoDialog(a.data?.get(pos))
        newFragment.show(getFragmentManager().beginTransaction(), "dialog")
    }


    private fun showInBrowser(pos: Int) {
        val i: Intent// = new Intent();
        i = Intent(Intent.ACTION_VIEW)
        val a = grid.getAdapter() as GridAdapter
        if (pos != -1) {
            Log.d("#", "pos=" + pos)
            i.setData(Uri.parse(a.data?.get(pos)?.get(Constants.DATA_URL_PAGE)))
            startActivity(i)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.im_refresh -> initViews(getCurrentPage(), true)
        }
        return super<Fragment>.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        // if (menu.findItem(R.id.im_refresh) == null)
        inflater!!.inflate(R.menu.viewer_menu, menu)
        super<Fragment>.onCreateOptionsMenu(menu, inflater)
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }


    override fun onPageSelected(position: Int) {
    }


    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            tempdata = null  // reset temp data on page swap
            val pos = viewPager!!.getCurrentItem()

            currPage = pos
            currPhoto = 0
            getRoot().currPage = currPage
            initViews(getCurrentPage(), false)
            getRoot().getSupportActionBar()!!.setSubtitle(getResources().getString(R.string.page_) + (currPage + 1))

            //Random r = new Random();
            L.v("page #" + pos)
        }
    }


    private fun getCurrentPage(): View {
        return viewPager!!.findViewWithTag(viewPager!!.getCurrentItem())
    }


    //
    // **********************************************************************************************************************************************
    // ViewerPagerAdapter
    // **********************************************************************************************************************************************
    //
    public class ViewerPagerAdapter(private val ctx: Context, val numOfPages: Int) : PagerAdapter() {

        override fun getCount(): Int {
            return numOfPages
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            L.v("item created " + position)
            val v = LayoutInflater.from(ctx).inflate(R.layout.viewer, null)
            v.setTag(position)
            container.addView(v)
            return v
        }


        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == (`object` as View)
        }


    }


    //
    // **********************************************************************************************************************************************
    // GridAdapter
    // **********************************************************************************************************************************************
    //
    private class GridAdapter(private val ctx: Context, public val data: List<Map<Int, String>>?, private val cellSize: Int) : BaseAdapter() {
        var numOfItems: Int = 0
        // private ArrayList<Map<Integer, String>> urlList = new ArrayList<Map<Integer, String>>();
        private val aq: AQuery
        private val imageLayout: FrameLayout.LayoutParams
        private val progressLayout: FrameLayout.LayoutParams
        private val fadeIn: Animation? = null


        init {
            //if (data == null)
            //   return
            this.numOfItems = data!!.size()
            imageLayout = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            val d = cellSize / 5
            progressLayout = FrameLayout.LayoutParams(d, d)
            progressLayout.gravity = Gravity.CENTER

            aq = AQuery(ctx)
        }


        override fun getCount(): Int {
            return numOfItems
        }


        override fun getItem(position: Int): Any? {
            return null
        }


        override fun getItemId(position: Int): Long {
            return 0
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view: FrameLayout? = convertView as FrameLayout?
            val h: ViewHolder

            if (view == null) {
                view = FrameLayout(ctx)
                view?.setLayoutParams(AbsListView.LayoutParams(cellSize, cellSize))

                h = ViewHolder(view)
                view?.setTag(h)
            } else {
                h = view?.getTag() as ViewHolder
                h.img.setImageBitmap(null)
            }


            if (!aq.shouldDelay(position, view, parent, data?.get(position)?.get(Constants.DATA_URL_SMALL)))
                aq.id(h.img).progress(h.progress).image(data?.get(position)?.get(Constants.DATA_URL_SMALL), true, true, 0, 0, null,
                        com.androidquery.util.Constants.FADE_IN)
            else {
                h.img.setVisibility(View.GONE)
                h.progress.setVisibility(View.VISIBLE)
            }

            //imageLoader.DisplayImage(data.get(position), h.img, h.progress, false);
            return view!!
        }


        private inner class ViewHolder(view: ViewGroup?) {

            var img: ImageView
            var progress: ProgressBar


            {

                img = ImageView(ctx)
                img.setScaleType(ScaleType.CENTER_CROP)
                img.setLayoutParams(imageLayout)
                //img.setAdjustViewBounds(true);

                progress = ProgressBar(ctx)
                progress.setLayoutParams(progressLayout)

                view?.addView(img)
                view?.addView(progress)
            }

        }
    }

    companion object {

        private val VIEWPAGER_ID = 123456789
    }

}
