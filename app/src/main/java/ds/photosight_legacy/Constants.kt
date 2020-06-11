package ds.photosight_legacy

// todo refactor this shit
interface Constants {
    companion object {

        val TAB_CATEGORIES: Int = 0
        val TAB_TOPS: Int = 1
        val TAB_AUTHORS: Int = 2

        val RESCALE_WIDTH: Int = 800
        val LIST_CLICKED: String = "list_clicked"

        val CATEGORIES_ID: IntArray = intArrayOf(82, 7, 12, 8, 5, 13, 15, 27, 65, 3, 19, 91, 92, 17, 11, 18, 80, 70, 14, 78, 4, 16, 36, 2, 6, 9, 10, 64, 87)
        val TOPS_IDS: IntArray = intArrayOf()
        val URL_CATEGORY: String = "http://www.photosight.ru/photos/category/%d/?pager=%d"    //count page
        val URL_MAIN: String = "http://www.photosight.ru"
        val ITEM_TOP_DAY: Int = 0
        val ITEM_TOP_WEEK: Int = 1
        val ITEM_TOP_MONTH: Int = 2
        val ITEM_TOP_ART: Int = 3
        val ITEM_TOP_ORIG: Int = 4
        val ITEM_TOP_TECH: Int = 5
        val ITEM_TOP_FAVORITES: Int = 6
        val ITEM_TOP_OUTRUN: Int = 7
        val URL_TOPS: Array<String> = arrayOf("http://www.photosight.ru/outrun/date/%d/%d/%d", //dateRaw page
                "http://www.photosight.ru/top/50/", //1 page
                "http://www.photosight.ru/top/200", //1 page
                "http://www.photosight.ru/top/art", //1 page
                "http://www.photosight.ru/top/orig", //1 page
                "http://www.photosight.ru/top/tech", //1 page
                "http://www.photosight.ru/top/favorites")//1 page
        //"http://www.photosight.ru/outrun",				//1 page

        public val PATH_SAVED_DEFAULT: String = "Photosight"

        val PREFS_KEY_SAVE_PATH = "savePath"
        val PREFS_KEY_CACHE_PATH = "cachePath"
        val PREFS_KEY_PARENTAL = "censored"
        val PREFS_KEY_CLEAR_CACHE = "clearCache"
        val PREFS_KEY_AUTO_CLEAR_CACHE = "autoClearCache"
        val PREFS_KEY_USE_INTERNAL_CACHE_DIR = "internalCache"
        val PREFS_KEY_COLUMNS = "thumbColumns"
        val PREFS_KEY_SHAREAPP = "shareApp"
        val PREFS_KEY_DONATE = "donate"
        val PREFS_KEY_LOW_RES = "lowres"
        val PREFS_KEY_ABOUT = "about"


        val DATA_URL_LARGE = 0
        val DATA_URL_SMALL: Int = 1
        val DATA_AUTHOR: Int = 2
        val DATA_IMG_NAME: Int = 3
        val DATA_URL_PAGE: Int = 4
        val DATA_AWARD: Int = 5
        val DATA_DATE: Int = 6


        //static final String SAMSUNG_APPS_URI = "samsungapps://ProductDetail/ds.photosight";
        val MARKET_OTHER: Int = 0
        val MARKET_AMAZON: Int = 1
        val MARKET_NAMES: Array<String> = arrayOf("other", "amazon")
        val MARKETS_URL_PRO: Array<String> = arrayOf("null",
                "null")
        val MARKETS_URL: Array<String> = arrayOf("null",
                "http://www.amazon.com/gp/mas/dl/android?p=ds.photosight&showAll=1"
        )

        val PRO_VERSION: Boolean = false
        val CURRENT_MARKET: Int = MARKET_AMAZON
    }


}
