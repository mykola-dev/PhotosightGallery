package ds.photosight.model

import java.io.IOException
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap

import android.os.AsyncTask
import ds.photosight.App
import ds.photosight.Constants
import ds.photosight.utils.L
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.String.format

class DataLoader(private val tab: Int, private val item: Int, private val page: Int) : Constants {
    //private int sort;
    private var mDoc: Document? = null
    private var mLargeImage: String? = null
    private var isCensored = false
    private val date: Calendar

    private var listener: OnLoadListener? = null
    var maxSize = 60


    init {
        isCensored = App.prefs.getBoolean("parental", false)
        //this.sort = sort;
        date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, -page)
        val task = GetHtmlTask()
        task.execute(getUrl())

    }


    private fun getUrl(): String {
        var request = ""
        // String sortString = SORT_STRINGS[sort];
        // parseParam2 = 1;

        when (tab) {
            Constants.TAB_CATEGORIES -> {
                request = format(Constants.URL_CATEGORY, Constants.CATEGORIES_ID[item], page + 1)
                // parseParam1 = 1;
                maxSize = 57
            }
            Constants.TAB_TOPS -> {
                // parseParam1 = 0;
                maxSize = MAX_SIZES[item]
                request = if (item == Constants.ITEM_TOP_DAY) {
                    // parseParam2 = 0;
                    format(Constants.URL_TOPS[item], date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH))
                    // Log.d("#", request);
                } else {
                    Constants.URL_TOPS[item]

                }
            }
            Constants.TAB_AUTHORS -> {
            }
        }

        //Log.d("request=", request);

        return request

    }


    private fun parseHtml(html: String): ArrayList<Map<Int, String>>? {

        val list = ArrayList<Map<Int, String>>()
        var map: MutableMap<Int, String>
        var link: String
        var icon: String
        var name: String
        var date: String
        var author: String
        var award: String
        var photoId: String

        // Document doc = Jsoup.parse(html);
        var root: Element? = null
        val e: Elements

        val photolist: Elements

        if (mDoc == null)
            return null

        if (tab == Constants.TAB_TOPS) {
            // search ul.bigphotolist
            root = mDoc!!.select("div.photos-content.cols").first()

        } else {
            root = mDoc!!.select("div.photos-content.cols").first()
        }

        if (root == null) {
            L.e("root is null")
            return null
        }

        photolist = root.children()
        L.v("photolist size=" + photolist.size)
        val replacement = if (App.isLowRes) "_large." else "_xlarge."
        for (photo in photolist) {
            photoId=photo.attr("data-photoid")
            map = HashMap<Int, String>()
            val a_hrefs = photo.select("a[href]")
            if (a_hrefs.size == 0)
                continue

            icon = a_hrefs.get(0).child(0).attr("src") // thumb url
            //link = a_hrefs.get(0).attr("href")
            link = "/photos/$photoId"
            name = a_hrefs.get(0).child(0).attr("alt") // имя фотки
            date = "Not supported yet"// photo.select("span.dateRaw").text(); //дата

            // award parsing
            award = ""
            /*
			 * e = photo.getElementsByAttributeValueStarting("src", "/images/icon/"); if (!e.isEmpty()) { award = e.get(0).attr("alt"); }
			 * else award = "";
			 */

           author=photo.select("a[href^=\"/users\"]").text()

            // if nude
            if (icon.contains("/skin/")) {
                icon = Constants.URL_MAIN + icon
                mLargeImage = icon
            } else {

                mLargeImage = icon.replace("_icon.", replacement).replace("pv_", "").replace("prv-", "img-").replace("_thumb.", replacement)
            }
            L.d("id " + photoId)
            L.d("links " + a_hrefs.size)
            L.d("link " + link)
            L.d("icon " + icon)
            L.d("name " + name)
            L.d("dateRaw " + date)
            L.d("author " + author)
            L.d("award " + award)

            map.put(Constants.DATA_IMG_NAME, name)
            map.put(Constants.DATA_URL_SMALL, icon)
            map.put(Constants.DATA_URL_LARGE, mLargeImage as String)
            map.put(Constants.DATA_URL_PAGE, Constants.URL_MAIN + link)
            map.put(Constants.DATA_DATE, date)
            map.put(Constants.DATA_AUTHOR, author)
            map.put(Constants.DATA_AWARD, award)
            list.add(map)

        }

        // Log.d("HERE!",""+photos.get(0).html());
        //L.v("size=" + list.size());

        return list
    }


    public fun setOnLoadListener(l: OnLoadListener) {
        listener = l
    }


    inner class GetHtmlTask : AsyncTask<String, Int, ArrayList<Map<Int, String>>>() {

        override fun doInBackground(vararg req: String): ArrayList<Map<Int, String>> {
            val html = ""
            // String html = downloadOld(req[0]);
            download(req[0])
            val urlList: ArrayList<Map<Int, String>>?
            urlList = parseHtml(html)
            return urlList!!
        }


        override fun onPostExecute(result: ArrayList<Map<Int, String>>) {
            listener!!.onLoad(result, page)

            super.onPostExecute(result)
        }


        fun onProgressUpdate(vararg values: Int) {
            if (values[0] != -1)
                listener!!.onProgress(page, values[0])
            else
                listener!!.onProgressEnd(page)
        }


        private fun download(urlString: String) {
            try {
                L.v("url=" + urlString)
                mDoc = Jsoup.connect(urlString).cookie("show_nude", if (isCensored) "0" else "1").cookie("adult_mode", if (isCensored) "0" else "1").get()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }


    public interface OnLoadListener {

        public fun onLoad(result: ArrayList<Map<Int, String>>, page: Int)


        public fun onProgress(page: Int, progress: Int)


        public fun onProgressEnd(page: Int)
    }

    companion object {

        private val MAX_SIZES = intArrayOf(30, 80, 270, 140, 140, 140, 80, 50)
    }

}
