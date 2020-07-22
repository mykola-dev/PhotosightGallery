/*
package ds.photosight_legacy.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.ImageView.ScaleType
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.androidquery.AQuery
import com.androidquery.util.AQUtility
import de.greenrobot.event.EventBus
import ds.photosight.R
import ds.photosight_legacy.App
import ds.photosight_legacy.Constants
import ds.photosight_legacy.event.PhotoInfo
import ds.photosight_legacy.model.DataLoader
import ds.photosight_legacy.model.DataLoader.OnLoadListener
import ds.photosight.utils.L
import java.util.ArrayList
import kotlin.properties.Delegates

class ViewerFragment : Fragment(), Constants, ViewPager.OnPageChangeListener {
    private val app: App? = null
    private var grid: GridView by Delegates.notNull()
    private var progress: View? = null
    private var currPage = 0
    private var currPhoto = 0
    private var viewerData: DataLoader? = null
    private var pageAdapter: ViewerPagerAdapter? = null
    private var viewPager: ViewPager? = null
    private var mColumnWidth: Int = 0
    private var aq: AQuery? = null
    private var tempdata: List<Map<Int, String>>? = null


    private fun getRoot(): MainActivity {
        return activity as MainActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        L.v("viewer onActivity created")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.v("Viewer view created!")

        aq = AQuery(activity)
        currPage = getRoot().currPage

        setupTitle()

        setHasOptionsMenu(true)

        pageAdapter = ViewerPagerAdapter(requireActivity(), getNumOfPages())
        viewPager!!.adapter = pageAdapter
        viewPager!!.setOnPageChangeListener(this)
        viewPager!!.setCurrentItem(currPage, false)
        AQUtility.postDelayed({ initViews(getCurrentPage(), false) }, 200)

    }


    override fun onResume() {
        super.onResume()
        L.v("viewer onresume")
        val info = EventBus.getDefault().getStickyEvent(PhotoInfo::class.java)
        if (info != null) {
            //T.show(getActivity(), "got some event!");
            getRoot().currentTab = info.tab
            getRoot().currPage = info.page
            currPage = info.page
            currPhoto = info.item
            L.v("curr photo: $currPhoto")
            tempdata = info.data
            if (tempdata != null) {
                viewPager!!.setCurrentItem(currPage, false)
                AQUtility.postDelayed({ initViews(getCurrentPage(), false) }, 200)
            }

            EventBus.getDefault().removeStickyEvent(info)
        }
    }


    private fun getNumOfPages(): Int = if (
            getRoot().currentTab == Constants.TAB_TOPS &&
            getRoot().getListSelection(Constants.TAB_TOPS) != Constants.ITEM_TOP_DAY
    ) 1
    else 100500


    private fun setupTitle() {
        val ab = getRoot().supportActionBar!!
        L.v("setting 2 line title")
        ab.setDisplayShowCustomEnabled(false)
        ab.setSubtitle(resources.getString(R.string.page_) + (currPage + 1))

    }


    fun reload() {
        tempdata = null
        pageAdapter = ViewerPagerAdapter(requireActivity(), getNumOfPages())
        viewPager!!.adapter = pageAdapter
        viewPager!!.setOnPageChangeListener(this)
        currPage = getRoot().currPage
        viewPager!!.setCurrentItem(currPage, false)
        initViews(getCurrentPage(), false)

    }


    private fun initViews(v: View, forceUpdate: Boolean) {
        setupTitle()
        grid = v.findViewById(R.id.viewerGrid) as GridView
        grid.setDrawSelectorOnTop(true)
        val gridWidth = grid.width
        L.v("grid width=$gridWidth")
        val numOfColumns = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(activity).getString(Constants.PREFS_KEY_COLUMNS, "2"))!!
        grid.numColumns = numOfColumns
        if (gridWidth != 0) {
            mColumnWidth = gridWidth / numOfColumns
            grid.columnWidth = mColumnWidth
        }

        if (grid.adapter != null && !forceUpdate) {
            L.w("grid.getAdapter() != null && !forceUpdate")
            if (currPhoto != 0) {
                grid.setSelection(currPhoto)
                //currPhoto = 0;
            }
            return
        }
        progress = v.findViewById(R.id.viewerLoader)

        if (tempdata != null) {
            progress!!.visibility = View.GONE
            loadThumbs(tempdata)
            tempdata = null
            return
        }

        progress!!.visibility = View.VISIBLE
        grid.visibility = View.GONE
        val tab = getRoot().currentTab
        viewerData = DataLoader(tab, getRoot().getListSelection(tab), currPage)
        viewerData!!.setOnLoadListener(object : OnLoadListener {

            override fun onLoad(result: ArrayList<Map<Int, String>>, page: Int) {
                if (activity == null)
                    return

                if (page != currPage)
                    return

                if (progress != null && activity != null) {
                    val a = AnimationUtils.loadAnimation(activity, R.anim.fade_out)
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
                Toast.makeText(activity, R.string.no_photos_try_next_page_, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(activity, R.string.connection_error, Toast.LENGTH_SHORT).show()
            return
        }
        grid.visibility = View.VISIBLE
        val adapter = GridAdapter(requireActivity(), result, mColumnWidth)
        grid.adapter = adapter
        if (currPhoto != 0) {
            L.v("selecting grid cell")
            grid.setSelection(currPhoto)
            currPhoto = 0
        }
        grid.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            val i = Intent(activity, GalleryActivity::class.java)
            // Bundle e;
            i.putExtra("data", data as ArrayList<Any>)
            i.putExtra("item", pos)
            i.putExtra("page", currPage)
            i.putExtra("tab", getRoot().currentTab)
            i.putExtra("category", getRoot().getCurrentListSelection())
            Log.d("startGallery", "item$pos page$currPage")
            startActivity(i)
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        registerForContextMenu(grid)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewPager = ViewPager(requireActivity())
        viewPager!!.id = VIEWPAGER_ID
        return viewPager
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        when (item.itemId) {
            R.id.imc_open_in_browser -> showInBrowser(pos)
            R.id.imc_show_detailed_info -> showInfo(pos)
        }
        return super.onContextItemSelected(item)
    }


    private fun showInfo(pos: Int) {
        val a = grid.adapter as GridAdapter
        val newFragment = InfoDialog(a.data?.get(pos))
        newFragment.show(parentFragmentManager.beginTransaction(), "dialog")
    }


    private fun showInBrowser(pos: Int) {
        val i = Intent(Intent.ACTION_VIEW)// = new Intent();
        val a = grid.adapter as GridAdapter
        if (pos != -1) {
            Log.d("#", "pos=$pos")
            i.data = Uri.parse(a.data?.get(pos)?.get(Constants.DATA_URL_PAGE))
            startActivity(i)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.im_refresh -> initViews(getCurrentPage(), true)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.viewer_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
    class ViewerPagerAdapter(private val ctx: Context, val numOfPages: Int) : PagerAdapter() {

        override fun getCount(): Int {
            return numOfPages
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            L.v("item created " + position)
            val v = LayoutInflater.from(ctx).inflate(R.layout.viewer, null)
            v.tag = position
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
            this.numOfItems = data!!.size
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
                view.layoutParams = AbsListView.LayoutParams(cellSize, cellSize)

                h = ViewHolder(view)
                view.tag = h
            } else {
                h = view.tag as ViewHolder
                h.img.setImageBitmap(null)
            }


            if (!aq.shouldDelay(position, view, parent, data?.get(position)?.get(Constants.DATA_URL_SMALL)))
                aq.id(h.img).progress(h.progress).image(data?.get(position)?.get(Constants.DATA_URL_SMALL), true, true, 0, 0, null,
                        com.androidquery.util.Constants.FADE_IN)
            else {
                h.img.visibility = View.GONE
                h.progress.visibility = View.VISIBLE
            }

            return view
        }


        private inner class ViewHolder(view: ViewGroup?) {

            var img: ImageView = ImageView(ctx)
            var progress: ProgressBar


            init {

                img.scaleType = ScaleType.CENTER_CROP
                img.layoutParams = imageLayout
                //img.setAdjustViewBounds(true);

                progress = ProgressBar(ctx)
                progress.layoutParams = progressLayout

                view?.addView(img)
                view?.addView(progress)
            }

        }
    }

    companion object {

        private const val VIEWPAGER_ID = 123456789
    }

}
*/
