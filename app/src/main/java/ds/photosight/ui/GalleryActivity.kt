package ds.photosight.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.androidquery.AQuery
import com.androidquery.AbstractAQuery
import com.androidquery.callback.AbstractAjaxCallback
import com.androidquery.callback.AjaxStatus
import com.androidquery.callback.BitmapAjaxCallback
import de.greenrobot.event.EventBus
import ds.photosight.App
import ds.photosight.Constants
import ds.photosight.R
import ds.photosight.event.PhotoInfo
import ds.photosight.model.AdvancedInfoParser
import ds.photosight.model.Comment
import ds.photosight.model.DataLoader
import ds.photosight.model.DataLoader.OnLoadListener
import ds.photosight.ui.widget.GalleryViewPager
import ds.photosight.ui.widget.VotesWidget
import ds.photosight.utils.L
import ds.photosight.utils.Utils
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale

//
//**********************************************************************************************************************************************
// GalleryActivity
//**********************************************************************************************************************************************
//
public class GalleryActivity : AppCompatActivity(), Constants, ViewPager.OnPageChangeListener {

    private var pager: GalleryViewPager? = null
    private var pagerAdapter: GalleryAdapter? = null
    private var data: ArrayList<Map<Int, String>>? = null
    private val dataSet = HashMap<Int, List<Map<Int, String>>>()
    private var currPhoto: Int = 0
    private var currPage: Int = 0
    private var currTab: Int = 0
    private var currCategory: Int = 0
    private var offset: Int = 0
    private var adCounter = 0
    //private InterstitialAd interstitial;
    private var aq: AQuery? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private val mComments: MutableMap<String, List<Comment>> = HashMap()
    private val mAwards: MutableMap<String, List<String>> = HashMap()
    private val mRates: MutableMap<String, List<Int>> = HashMap()
    private var mVotesView: VotesWidget? = null
    public var actionBarAutoHide: Boolean = true
    private val AD_FREQ = 2


    override fun onCreate(state: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(state)

        val l = Locale.getDefault().isO3Language
        L.d("locale=$l")

        aq = AQuery(this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE

        // setContentView(R.layout.gallery);
        val b = intent.extras
        if (state != null) {
            data = state.getSerializable("data") as ArrayList<Map<Int, String>>
            currPage = state.getInt("page")
            currPhoto = state.getInt("item")
            currTab = state.getInt("tab")
            currCategory = state.getInt("category")
            val comments: List<Comment>? = state.getSerializable("comments") as ArrayList<Comment>?
            val rates: List<Int>? = state.getSerializable("rates") as ArrayList<Int>?
            val awards: List<String>? = state.getSerializable("awards") as ArrayList<String>?
            if (comments != null && awards != null && rates != null) {
                mComments[getCurrentItem()[Constants.DATA_URL_PAGE] as String] = comments
                mAwards[getCurrentItem()[Constants.DATA_URL_PAGE] as String] = awards
                mRates[getCurrentItem()[Constants.DATA_URL_PAGE] as String] = rates
            }
        } else {
            data = b.get("data") as ArrayList<Map<Int, String>>
            currTab = b.getInt("tab")
            currPage = b.getInt("page")
            currPhoto = b.getInt("item")
            currCategory = b.getInt("category")
        }

        var t = ""
        when (currTab) {
            Constants.TAB_CATEGORIES -> t = getResources().getStringArray(R.array.categories_array)[currCategory]
            Constants.TAB_TOPS -> t = getString(R.string.top_in) + getResources().getStringArray(R.array.tops_array)[currCategory]
        }


        setContentView(R.layout.gallery)
        pager = findViewById(R.id.pager)
        pager!!.setOnPageChangeListener(this)
        //pager.setOnClickListener(mPagerClickListener);


        mDrawerLayout = findViewById(R.id.drawer_layout)
        mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                actionBarAutoHide = true
                aq!!.id(R.id.drawer_progress).visible()
                aq!!.id(R.id.drawer_list).gone()
                supportActionBar!!.hide()
            }


            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                loadComments()
            }
        }

        mDrawerLayout!!.setDrawerListener(mDrawerToggle)

        val bar = supportActionBar!!
        bar.setDisplayHomeAsUpEnabled(true)
        bar.setHomeButtonEnabled(true)
        bar.setIcon(R.drawable.icon_white2)
        bar.setDisplayShowCustomEnabled(true)
        bar.setCustomView(R.layout.ab_title2)
        refreshTitle(t)
        bar.setBackgroundDrawable(ColorDrawable(1426063360))
        bar.setSplitBackgroundDrawable(ColorDrawable(1426063360))
        bar.hide()

        initPager(currPhoto)


    }


    private fun showComments(comments: List<Comment>, rates: List<Int>, avards: List<String>) {
        aq!!.id(R.id.drawer_progress).gone()
        aq!!.id(R.id.drawer_list).visible()
        if (aq!!.listView.headerViewsCount == 0) {
            //final View header = new View(this);
            /*AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dp(48));
			header.setLayoutParams(params);
			aq.getListView().addHeaderView(header);*/
            aq!!.listView.setHeaderDividersEnabled(false)

            mVotesView = layoutInflater.inflate(R.layout.votes, null) as VotesWidget
            aq!!.listView.addHeaderView(mVotesView)

        }

        mVotesView!!.setRates(rates)
        mVotesView!!.setAvards(avards)
        mVotesView!!.runAnimations()
        aq!!.adapter(CommentsAdapter(this, comments))
        //aq.getListView().setDivider(new DottedDivider());

    }


    private fun loadComments() {
        actionBarAutoHide = false
        supportActionBar!!.hide()
        val url = getCurrentItem()[Constants.DATA_URL_PAGE]
        if (mComments.containsKey(url)) {
            showComments(mComments[url] as List<Comment>, mRates[url] as List<Int>, mAwards[url] as List<String>)
            return
        }

        L.v("loading page: $url")
        AdvancedInfoParser().parseAsync(url as String, object : AdvancedInfoParser.Callback {

            override fun onDone(comments: List<Comment>, rates: List<Int>, awards: List<String>) {
                L.v("done ${comments.size}")
                mComments[url] = comments
                mRates[url] = rates
                mAwards[url] = awards
                if (getCurrentItem()[Constants.DATA_URL_PAGE] == url) {
                    showComments(comments, rates, awards)
                }
            }
        })

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            loadComments()
            L.v("drawer is opened")
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)

    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putInt("page", currPage)
        state.putInt("item", currPhoto)
        state.putInt("tab", currTab)
        state.putSerializable("data", data as ArrayList<Any>)
        state.putInt("offset", offset)

        val url = getCurrentItem()[Constants.DATA_URL_PAGE]
        if (mComments.containsKey(url)) {
            state.putSerializable("comments", mComments[url] as ArrayList<Any>)
            state.putSerializable("rates", mRates[url] as ArrayList<Any>)
            state.putSerializable("awards", mAwards[url] as ArrayList<Any>)
        }

        state.putInt("category", currCategory)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.gallery_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val i: Intent

        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }


        when (item!!.itemId) {
            R.id.im_share_link -> showShareLink()
            R.id.im_share_img -> showShareImage()
            R.id.im_save_sdcard -> {

                saveImage()
                showAd()
            }
            R.id.im_open_in_browser -> {
                i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(getCurrentItem()[Constants.DATA_URL_PAGE])
                startActivity(i)
            }
            R.id.im_info -> showInfo()
            R.id.im_wallpaper -> setWallpaper2()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setWallpaper2() {

        val uri = Uri.parse(getCurrentItem()[Constants.DATA_URL_LARGE])
        val f = aq!!.makeSharedFile(uri.toString(), uri.lastPathSegment)
        val i = Intent(Intent.ACTION_ATTACH_DATA)
        i.setDataAndType(Uri.fromFile(f), "image/*")
        i.putExtra("mimeType", "image/*")
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f))
        startActivity(Intent.createChooser(i, getString(R.string.set_as)))
    }


    private fun getCurrentItem(): Map<Int, String> {
        return data!![currPhoto]
    }


    private fun showInfo() {
        val newFragment = InfoDialog(getCurrentItem())
        newFragment.show(supportFragmentManager.beginTransaction(), "dialog")
    }


    private fun saveImage() {
        val path = File(Environment.getExternalStorageDirectory(), App.prefs.getString(Constants.PREFS_KEY_SAVE_PATH, Constants.PATH_SAVED_DEFAULT))
        val message: String
        val uri = Uri.parse(getCurrentItem()[Constants.DATA_URL_LARGE])
        val filename = uri.lastPathSegment
        val src = aq!!.getCachedFile(uri.toString())
        L.v("saving $uri")
        val dst = File(path, filename)
        if (src != null) {
            val b = Utils.copyFiles(src, dst)
            if (b) {
                message = getString(R.string.saved_to_) + path
            } else {
                message = getString(R.string.failed)
                L.e("file copying filed")
            }
        } else {
            message = getString(R.string.failed)
            L.e("cached image not found!")
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showAd() {
        if (Constants.PRO_VERSION)
            return

    }


    private fun showShareLink() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj))
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + getCurrentItem()[Constants.DATA_URL_PAGE])
        startActivity(Intent.createChooser(share, getString(R.string.share_link)))

    }


    private fun showShareImage() {
        val uri = Uri.parse(getCurrentItem()[Constants.DATA_URL_LARGE])
        val file = aq!!.makeSharedFile(uri.toString(), uri.lastPathSegment)
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj))
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        startActivity(Intent.createChooser(share, getString(R.string.share_img)))

    }


    private fun needAd(): Boolean {
        adCounter++
        if (adCounter % AD_FREQ == 0 && offset == 1)
            return true
        return false
    }


    private fun loadNewPage() {
        Toast.makeText(this, R.string.loading_next_page, Toast.LENGTH_SHORT).show()

        // final int newItem;
        if (!dataSet.containsKey(currPage)) {
            dataSet[currPage] = data!!
        }

        val direction = if ((currPhoto + offset) == 0) -1 else 1
        currPage += direction

        if (!dataSet.containsKey(currPage)) {
            val loader = DataLoader(currTab, currCategory, currPage)
            loader.setOnLoadListener(object : OnLoadListener {

                override fun onLoad(result: ArrayList<Map<Int, String>>, page: Int) {
                    data = result
                    currPhoto = if (direction == -1) data!!.size - 1 else 0
                    initPager(currPhoto)
                    if (needAd()) {
                        showAd()
                    }
                }


                override fun onProgress(page: Int, progress: Int) {
                }


                override fun onProgressEnd(page: Int) {
                }

            })
        } else {
            data = dataSet[currPage] as ArrayList<Map<Int, String>>?
            currPhoto = if (direction == -1) data!!.size - 1 else 0
            initPager(currPhoto)
        }

    }


    private fun initPager(item: Int) {
        if (data == null) {
            Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show()
            return
        }
        offset = if (currPage == 0) 0 else 1

        pagerAdapter = GalleryAdapter(this, data!!, data!!.size + offset + 1)
        pager!!.setAdapter(pagerAdapter)
        pager!!.setCurrentItem(item + offset, false)
        refreshTitle(null)
    }


    private fun closeActivity() {
        //T.show(this,"prepare event...");
        EventBus.getDefault().postSticky(PhotoInfo(currTab, currCategory, currPage, currPhoto, data))
        //finish();
    }


    override fun onBackPressed() {
        closeActivity()
        super<AppCompatActivity>.onBackPressed()
    }


    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            val pos = pager!!.getCurrentItem()
            currPhoto = pos - offset
            if ((pos == 0 && currPage != 0) || pos == data!!.size + offset) {
                loadNewPage()
                return
            }

            refreshTitle(null)
        }
    }


    private fun refreshTitle(title: String?) {
        val aq = AQuery(supportActionBar!!.customView)
        if (title != null)
            aq.id(R.id.text1).text(title)
        //aq.id(R.id.text2).text(String.format("[%s:%s] %s", currPage + 1, currPhoto + 1, getCurrentItem().get(Constants.DATA_IMG_NAME)))
        aq.id(R.id.text2).text("[${currPage + 1}:${currPhoto + 1}] ${getCurrentItem().get(Constants.DATA_IMG_NAME)}")

    }


    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
    }


    override fun onPageSelected(pos: Int) {
    }


    //
    // **********************************************************************************************************************************************
    // GalleryAdapter
    // **********************************************************************************************************************************************
    //
    public class GalleryAdapter(private val ctx: Context, private val data: List<Map<Int, String>>, private val numOfItems: Int) : PagerAdapter() {
        private val ACTION_BAR_DELAY = 5000
        private val aq: AQuery
        private var mCurrentView: View? = null
        private val offset: Int
        private val mImageWidth: Int
        private val mAttacher: PhotoViewAttacher? = null


        private val mPagerClickListener = object : GalleryViewPager.ClickListener {

            var h = Handler()
            var r: Runnable = object : Runnable {

                override fun run() {
                    hide((ctx as AppCompatActivity).supportActionBar!!)
                }
            }


            override fun onClick() {
                L.v("onClick")
                val ab = (ctx as AppCompatActivity).supportActionBar!!
                if (!ab.isShowing) {
                    show(ab)
                    h.postDelayed(r, ACTION_BAR_DELAY.toLong())
                } else {
                    h.removeCallbacks(r)
                    hide(ab)

                }
            }


            private fun show(ab: ActionBar) {
                ab.show()
                (ctx as AppCompatActivity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }


            private fun hide(ab: ActionBar) {
                ab.hide()
                (ctx as AppCompatActivity).window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
            }
        }

        private inner class OnDoubleTapListener (var photo: PhotoView?) : GestureDetector.OnDoubleTapListener {


            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                L.v("onSingleTapConfirmed")
                mPagerClickListener.onClick()
                return false
            }


            override fun onDoubleTap(e: MotionEvent): Boolean {
                L.v("onDoubleTap")

                if (photo == null)
                    return false

                try {
                    val scale: Float// = photo.getScale();
                    val x = e.getX()
                    val y = e.getY()

                    val rect = photo!!.getDisplayRect()
                    val w = rect.width().toInt()
                    val h = rect.height().toInt()
                    L.v("w=" + w + " h=" + h)

                    if (w < photo!!.getWidth())
                        scale = photo!!.getWidth().toFloat() / w.toFloat()
                    else if (h < photo!!.getHeight())
                        scale = photo!!.getHeight().toFloat() / h.toFloat()
                    else
                        scale = photo!!.getMinimumScale()

                    photo!!.setScale(scale, x, y, true)

                } catch (ex: ArrayIndexOutOfBoundsException) {
                    // Can sometimes happen when getX() and getY() is called
                    ex.printStackTrace()
                }


                return true
            }


            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                L.v("onDoubleTapEvent")
                return false
            }
        }


        init {
            aq = AQuery(ctx)
            offset = numOfItems - data.size - 1
            mImageWidth = if (App.prefs.getBoolean("rescale", false)) Constants.RESCALE_WIDTH else 0

        }


        override fun instantiateItem(container: ViewGroup, pos: Int): Any {
            val position = pos - offset

            val v = LayoutInflater.from(ctx).inflate(R.layout.gallery_fragment, null)
            container.addView(v, 0)

            if (((offset == 1) && pos == 0) || pos >= numOfItems - 1) {
                return v
            }

            val item = data.get(position)
            aq.recycle(v)
            val thumb = aq.getCachedImage(item[Constants.DATA_URL_SMALL])
            val url = item[Constants.DATA_URL_LARGE] as String
            L.v("img url=$url")
            val img = aq.id(R.id.gallery_image).view as PhotoView

            img.setOnDoubleTapListener(OnDoubleTapListener(img))
            img.maximumScale = 10f

            val cb = object : BitmapAjaxCallback() {

                override fun callback(url: String?, iv: ImageView, bm: Bitmap?, status: AjaxStatus) {
                    if (status.code == 200) {
                        iv.setImageBitmap(bm)
                        //L.v("retry count=" + getRetry())
                        if (thumb != null) {
                            L.v("thumb found")
                        } else {
                            L.w("no thumb. loading big image immediatelly")
                            val anim = AlphaAnimation(0f, 1f)
                            anim.interpolator = DecelerateInterpolator()
                            anim.duration = 500
                            iv.startAnimation(anim)

                        }
                    } else {
                        L.e("error loading photo")
                    }
                }

            }
            cb.preset(thumb)
            cb.retry(5)

            loadImage(url, cb)

            return v
        }


        private fun loadImage(url: String, cb: BitmapAjaxCallback) {
            aq.progress(R.id.gallery_progress).image(url, true, true, mImageWidth, 0, cb)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {

            mCurrentView = obj as View

        }


        public fun getCurrentImg(): ImageView? {
            if (mCurrentView != null) {
                val i = mCurrentView!!.findViewById(R.id.gallery_image) as ImageView
                return i
            }
            return null
        }


        override fun getCount(): Int {
            return numOfItems
        }


        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == (`object` as View)
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private inner class CommentsAdapter(context: Context, objects: List<Comment>) : ArrayAdapter<Comment>(context, R.layout.row_comment, R.id.content, objects) {

        var aq: AQuery
        var user: Bitmap


        init {
            aq = AQuery(context)
            user = BitmapFactory.decodeResource(context.resources, R.drawable.anonymous)
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            val c = getItem(position)
            aq.recycle(v)
            aq.id(R.id.avatar).image(c.avatarUrl, true, true, 0, R.drawable.anonymous, null, com.androidquery.util.Constants.FADE_IN)
            aq.id(R.id.name).text(c.name)
            aq.id(R.id.content).text(c.content)
            aq.id(R.id.date).text(if (c.dateFormatted != null) c.dateFormatted else c.dateRaw)
            if (c.status != null)
                aq.id(R.id.status).image(v.resources.getIdentifier(c.status, "drawable", v.context.packageName)).visible()
            else
                aq.id(R.id.status).gone()

            when {
                c.rate!!.startsWith("0") -> aq.id(R.id.rating).gone()
                c.rate!!.startsWith("-") -> aq.id(R.id.rating).text(c.rate).textColor(v.resources.getColor(R.color.red)).visible()
                else -> aq.id(R.id.rating).text("+" + c.rate).textColor(v.resources.getColor(R.color.green)).visible()
            }

            return v
        }

    }


}
