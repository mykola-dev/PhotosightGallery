package ds.photosight;

public interface Constants {
	public static final String FLURRY="YTU1ZAFD3EP1W4M9CQZ2";


	public static final int TAB_CATEGORIES = 0;
	public static final int TAB_TOPS = 1;
	public static final int TAB_AUTHORS = 2;
	
	public static final String LOGIN="photosight-gallery";
	public static final String PASSWORD="androiduser";
	
	public final static int RESCALE_WIDTH = 800;
	public final static String LIST_CLICKED="list_clicked";

	public static final int[] CATEGORIES_ID = { 82, 7, 12, 8, 5, 13, 15, 27, 65, 3, 19, 91, 92, 17, 11, 18, 80, 70, 14, 78, 4, 16, 36, 2, 6, 9, 10, 64, 87 };
	// public static final String[] TOPS = {};
	public static final int[] TOPS_IDS = {};
	public static final String URL_CATEGORY = "http://www.photosight.ru/photos/category/%d/?pager=%d";	//count page
	public static final String URL_MAIN = "http://www.photosight.ru";
	public static final int ITEM_TOP_DAY=0;
	public static final int ITEM_TOP_WEEK = 1;
	public static final int ITEM_TOP_MONTH= 2;
	public static final int ITEM_TOP_ART=3;
	public static final int ITEM_TOP_ORIG=4;
	public static final int ITEM_TOP_TECH=5;
	public static final int ITEM_TOP_FAVORITES=6;
	public static final int ITEM_TOP_OUTRUN=7;
	public static final String[] URL_TOPS={
		"http://www.photosight.ru/outrun/date/%d/%d/%d",//dateRaw page
		"http://www.photosight.ru/top/50/",				//1 page
		"http://www.photosight.ru/top/200",				//1 page
		"http://www.photosight.ru/top/art",				//1 page
		"http://www.photosight.ru/top/orig",			//1 page
		"http://www.photosight.ru/top/tech",			//1 page
		"http://www.photosight.ru/top/favorites",		//1 page
		//"http://www.photosight.ru/outrun",				//1 page
	};
	
	public static final String PATH_SAVED_DEFAULT = "Photosight";
	
	public static final String PREFS_KEY_SAVE_PATH = "savePath";
	public static final String PREFS_KEY_CACHE_PATH = "cachePath";
	public static final String PREFS_KEY_PARENTAL = "censored";
	public static final String PREFS_KEY_CLEAR_CACHE = "clearCache";
	public static final String PREFS_KEY_AUTO_CLEAR_CACHE = "autoClearCache";
	public static final String PREFS_KEY_USE_INTERNAL_CACHE_DIR = "internalCache";
	
	
	static final int DATA_URL_LARGE=0;
	static final int DATA_URL_SMALL=1;
	static final int DATA_AUTHOR=2;
	static final int DATA_IMG_NAME=3;
	static final int DATA_URL_PAGE=4;
	static final int DATA_AWARD=5;
	static final int DATA_DATE=6;
	public static final String[] AWARD_ICONS = { "1d","n","1n","m","1m","h","o","t","50","200" };
	static final int AWARD_1D=0;
	static final int AWARD_N=1;
	static final int AWARD_1N=2;
	static final int AWARD_M=3;
	static final int AWARD_1M=4;
	static final int AWARD_H=5;
	static final int AWARD_O=6;
	static final int AWARD_T=7;
	static final int AWARD_50=8;
	static final int AWARD_200=9;
	
	static final int SORT_NONE=0;
	static final int SORT_RECOMMENDATIONS=1;
	static final int SORT_ARTISTIC=2;
	static final int SORT_ORIGINAL=3;
	static final int SORT_TECHNICAL=4;
	
	static String[] SORT_STRINGS={
		"",
		"sort=recommendations&",
		"sort=ra&",
		"sort=ro&",
		"sort=rt&",
	};
	
	
	//static final String SAMSUNG_APPS_URI = "samsungapps://ProductDetail/ds.photosight";
	static final int MARKET_OTHER = 0;
	static final int MARKET_GOOGLE = 1;
	static final int MARKET_SAMSUNG = 2;
	static final int MARKET_AMAZON = 3;
	static final int MARKET_YANDEX = 4;
	static final String[] MARKET_NAMES = { "other", "google", "samsung", "amazon", "yandex" };
	static final String[] MARKETS_URL_PRO = {
		"market.android.com/details?id=ds.photosight",
		"null", 
		"null",
		"null",
		"null"};
	static final String[] MARKETS_URL = { 
		"https://play.google.com/store/apps/details?id=ds.photosight",
		"https://play.google.com/store/apps/details?id=ds.photosight",
		"null",
		"http://www.amazon.com/gp/mas/dl/android?p=ds.photosight&showAll=1",
		"https://play.google.com/store/apps/details?id=ds.photosight"};

	static final boolean PRO_VERSION = false;
	static final boolean CENSORED_VERSION = false;
	static final int CURRENT_MARKET = MARKET_GOOGLE;

}
