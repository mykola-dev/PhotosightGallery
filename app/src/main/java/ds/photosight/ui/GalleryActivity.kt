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
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
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
public class GalleryActivity : AppCompatActivity(), Constants, OnPageChangeListener {

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super<AppCompatActivity>.onCreate(state)

        val l = Locale.getDefault().getISO3Language()
        Log.d("#", "locale=" + l)

        aq = AQuery(this)

        if (Build.VERSION.SDK_INT >= 11)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE)

        // setContentView(R.layout.gallery);
        val b = getIntent().getExtras()
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
                mComments.put(getCurrentItem().get(Constants.DATA_URL_PAGE) as String, comments)
                mAwards.put(getCurrentItem().get(Constants.DATA_URL_PAGE) as String, awards)
                mRates.put(getCurrentItem().get(Constants.DATA_URL_PAGE) as String, rates)
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
        pager = findViewById(R.id.pager) as GalleryViewPager//new ds.photosight.ui.widget.GalleryViewPager(this);
        pager!!.setOnPageChangeListener(this)
        //pager.setOnClickListener(mPagerClickListener);


        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                actionBarAutoHide = true
                aq!!.id(R.id.drawer_progress).visible()
                aq!!.id(R.id.drawer_list).gone()
                getSupportActionBar()!!.hide()
            }


            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                loadComments()
            }
        }

        mDrawerLayout!!.setDrawerListener(mDrawerToggle)

        val bar = getSupportActionBar()
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
        if (aq!!.getListView().getHeaderViewsCount() == 0) {
            //final View header = new View(this);
            /*AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dp(48));
			header.setLayoutParams(params);
			aq.getListView().addHeaderView(header);*/
            aq!!.getListView().setHeaderDividersEnabled(false)

            mVotesView = getLayoutInflater().inflate(R.layout.votes, null) as VotesWidget
            aq!!.getListView().addHeaderView(mVotesView)

        }

        mVotesView!!.setRates(rates)
        mVotesView!!.setAvards(avards)
        mVotesView!!.runAnimations()
        aq!!.adapter(CommentsAdapter(this, comments))
        //aq.getListView().setDivider(new DottedDivider());

    }


    private fun loadComments() {
        actionBarAutoHide = false
        getSupportActionBar()!!.hide()
        val url = getCurrentItem().get(Constants.DATA_URL_PAGE)
        if (mComments.containsKey(url)) {
            showComments(mComments.get(url) as List<Comment>, mRates.get(url) as List<Int>, mAwards.get(url) as List<String>)
            return
        }

        L.v("loading page: " + url)
        AdvancedInfoParser().parseAsync(url as String, object : AdvancedInfoParser.Callback {

            override fun onDone(comments: List<out Comment>, rates: List<out Int>, awards: List<out String>) {
                L.v("done " + (if (comments != null) comments.size() else "null"))
                mComments.put(url, comments)
                mRates.put(url, rates)
                mAwards.put(url, awards)
                if (getCurrentItem().get(Constants.DATA_URL_PAGE) == url) {
                    showComments(comments, rates, awards)
                }
            }
        })

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
        if (mDrawerLayout!!.isDrawerOpen(Gravity.START)) {
            loadComments()
            L.v("drawer is opened")
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super<AppCompatActivity>.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)

    }


    override fun onDestroy() {

        super<AppCompatActivity>.onDestroy()
    }


    override fun onSaveInstanceState(state: Bundle) {
        super<AppCompatActivity>.onSaveInstanceState(state)
        state.putInt("page", currPage)
        state.putInt("item", currPhoto)
        state.putInt("tab", currTab)
        state.putSerializable("data", data as ArrayList<Any>)
        state.putInt("offset", offset)

        val url = getCurrentItem().get(Constants.DATA_URL_PAGE)
        if (mComments != null && mComments!!.containsKey(url)) {
            state.putSerializable("comments", mComments!!.get(url) as ArrayList<Any>)
            state.putSerializable("rates", mRates!!.get(url) as ArrayList<Any>)
            state.putSerializable("awards", mAwards!!.get(url) as ArrayList<Any>)
        }

        state.putInt("category", currCategory)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super<AppCompatActivity>.onCreateOptionsMenu(menu)
        getMenuInflater().inflate(R.menu.gallery_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val i: Intent

        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }


        when (item!!.getItemId()) {
            R.id.im_share_link -> showShareLink()
            R.id.im_share_img -> showShareImage()
            R.id.im_save_sdcard -> {

                saveImage()
                showAd()
            }
            R.id.im_open_in_browser -> {
                i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(getCurrentItem().get(Constants.DATA_URL_PAGE)))
                startActivity(i)
            }
            R.id.im_info -> showInfo()
            R.id.im_wallpaper -> setWallpaper2()
        }/*case android.R.id.home:
				closeActivity();
				break;*/
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }


    private fun setWallpaper2() {

        val uri = Uri.parse(getCurrentItem().get(Constants.DATA_URL_LARGE))
        val f = aq!!.makeSharedFile(uri.toString(), uri.getLastPathSegment())
        val i = Intent(Intent.ACTION_ATTACH_DATA)
        i.setDataAndType(Uri.fromFile(f), "image/*")
        i.putExtra("mimeType", "image/*")
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f))
        startActivity(Intent.createChooser(i, getString(R.string.set_as)))
    }


    private fun getCurrentItem(): Map<Int, String> {
        return data!!.get(currPhoto)
    }


    private fun showInfo() {
        val newFragment = InfoDialog(getCurrentItem())
        newFragment.show(getSupportFragmentManager().beginTransaction(), "dialog")
    }


    private fun saveImage() {
        val path = File(Environment.getExternalStorageDirectory(), App.getPrefs()!!.getString(Constants.PREFS_KEY_SAVE_PATH, Constants.PATH_SAVED_DEFAULT))
        val message: String
        val uri = Uri.parse(getCurrentItem().get(Constants.DATA_URL_LARGE))
        val filename = uri.getLastPathSegment()
        val src = aq!!.getCachedFile(uri.toString())
        L.v("saving " + uri.toString())
        val dst = File(path, filename)
        if (src != null) {
            val b = Utils.copyFiles(src, dst)
            if (b)
                message = getString(R.string.saved_to_) + path
            else {
                message = getString(R.string.failed)
                L.e("file copying filed")
            }
        } else {
            message = getString(R.string.failed)
            L.e("cached image not found!")
        }

        Toast.makeText(this, message, 0).show()
    }


    private fun showAd() {
        if (Constants.PRO_VERSION)
            return

    }


    private fun showShareLink() {
        val share = Intent(Intent.ACTION_SEND)
        share.setType("text/plain")
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj))
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + getCurrentItem().get(Constants.DATA_URL_PAGE))
        startActivity(Intent.createChooser(share, getString(R.string.share_link)))

    }


    private fun showShareImage() {
        val uri = Uri.parse(getCurrentItem().get(Constants.DATA_URL_LARGE))
        val file = aq!!.makeSharedFile(uri.toString(), uri.getLastPathSegment())
        val share = Intent(Intent.ACTION_SEND)
        share.setType("image/*")
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
        Toast.makeText(this, R.string.loading_next_page, 0).show()

        // final int newItem;
        if (!dataSet.containsKey(currPage)) {
            dataSet.put(currPage, data)
        }

        val direction = if ((currPhoto + offset) == 0) -1 else 1
        currPage += direction

        if (!dataSet.containsKey(currPage)) {
            val loader: DataLoader
            loader = DataLoader(currTab, currCategory, currPage)
            loader.setOnLoadListener(object : OnLoadListener {

                override fun onLoad(result: ArrayList<Map<Int, String>>, page: Int) {
                    data = result
                    currPhoto = if (direction == -1) data!!.size() - 1 else 0
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
            data = dataSet.get(currPage) as ArrayList<Map<Int, String>>?
            currPhoto = if (direction == -1) data!!.size() - 1 else 0
            initPager(currPhoto)
        }

    }


    private fun initPager(item: Int) {
        if (data == null) {
            Toast.makeText(this, R.string.connection_error, 0).show()
            return
        }
        offset = if (currPage == 0) 0 else 1

        pagerAdapter = GalleryAdapter(this, data!!, data!!.size() + offset + 1)
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
            if ((pos == 0 && currPage != 0) || pos == data!!.size() + offset) {
                loadNewPage()
                return
            }

            refreshTitle(null)
        }
    }


    private fun refreshTitle(title: String?) {
        val aq = AQuery(getSupportActionBar()!!.getCustomView())
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
                    hide((ctx as AppCompatActivity).getSupportActionBar())
                }
            }


            override fun onClick() {
                L.v("onClick")
                val ab = (ctx as AppCompatActivity).getSupportActionBar()
                if (!ab.isShowing()) {
                    show(ab)
                    h.postDelayed(r, ACTION_BAR_DELAY.toLong())
                } else {
                    h.removeCallbacks(r)
                    hide(ab)

                }
            }


            private fun show(ab: ActionBar) {
                ab.show()
                if (Build.VERSION.SDK_INT >= 11)
                    (ctx as AppCompatActivity).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
            }


            private fun hide(ab: ActionBar) {
                ab.hide()
                if (Build.VERSION.SDK_INT >= 11)
                    (ctx as AppCompatActivity).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE)
            }
        }
        //private int failCount = 0;


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
            offset = numOfItems - data.size() - 1
            mImageWidth = if (App.getPrefs()!!.getBoolean("rescale", false)) Constants.RESCALE_WIDTH else 0

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
            val thumb = aq.getCachedImage(item.get(Constants.DATA_URL_SMALL))
            val url = item.get(Constants.DATA_URL_LARGE) as String
            L.v("img url=" + url)
            val img = aq.id(R.id.gallery_image).getView() as PhotoView

            img.setOnDoubleTapListener(OnDoubleTapListener(img))
            img.setMaximumScale(10f)

            val cb = object : BitmapAjaxCallback() {

                override fun callback(url: String?, iv: ImageView, bm: Bitmap?, status: AjaxStatus?) {
                    //super.callback(url, iv, bm, status);
                    if (status!!.getCode() == 200) {
                        //failCount = 0;
                        iv.setImageBitmap(bm)
                        L.v("retry count=" + getRetry())
                        if (thumb != null) {
                            L.v("thumb found")
                        } else {
                            L.w("no thumb. loading big image immediatelly")
                            val anim = AlphaAnimation(0f, 1f)
                            anim.setInterpolator(DecelerateInterpolator())
                            anim.setDuration(500)
                            iv.startAnimation(anim)

                        }
                    } else {
                        L.e("error loading photo")
                    }
                }


                private fun getRetry(): Int {
                    try {
                        val f = javaClass<AbstractAjaxCallback<Any, Any>>().getDeclaredField("retry")
                        f.setAccessible(true)
                        return f.getInt(this)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                        return -1
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                        return -1
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
            // super.destroyItem(container,position,object);
        }

        /*
		 * void setOnSetPageListener(OnSetPageListener l) { listener = l; }
		 */

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private inner class CommentsAdapter(context: Context, objects: List<Comment>) : ArrayAdapter<Comment>(context, R.layout.row_comment, R.id.content, objects) {

        var aq: AQuery
        var user: Bitmap


        init {
            aq = AQuery(context)
            user = BitmapFactory.decodeResource(context.getResources(), R.drawable.anonymous)
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
                aq.id(R.id.status).image(v.getResources().getIdentifier(c.status, "drawable", v.getContext().getPackageName())).visible()
            else
                aq.id(R.id.status).gone()

            if (c.rate!!.startsWith("0"))
                aq.id(R.id.rating).gone()
            else if (c.rate!!.startsWith("-"))
                aq.id(R.id.rating).text(c.rate).textColor(v.getResources().getColor(R.color.red)).visible()
            else
                aq.id(R.id.rating).text("+" + c.rate).textColor(v.getResources().getColor(R.color.green)).visible()

            return v
        }

    }


}
