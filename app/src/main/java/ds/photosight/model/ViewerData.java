package ds.photosight.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import ds.photosight.App;
import ds.photosight.Constants;
import ds.photosight.utils.L;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ViewerData implements Constants {

	private static final int[] MAX_SIZES = {30, 80, 270, 140, 140, 140, 80, 50};
	private int tab;
	private int item;
	private int page;
	//private int sort;
	private Document mDoc;
	private String mLargeImage;
	private boolean isCensored = false;
	private Calendar date;

	private OnLoadListener listener;
	int maxSize = 60;


	public ViewerData(int tab, int item, int page) {
		isCensored = App.getPrefs().getBoolean("parental", false);
		this.tab = tab;
		this.item = item;
		this.page = page;
		//this.sort = sort;
		date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -page);
		GetHtmlTask task = new GetHtmlTask();
		task.execute(getUrl());

	}


	private String getUrl() {
		String request = "";
		// String sortString = SORT_STRINGS[sort];
		// parseParam2 = 1;

		switch (tab) {
			case TAB_CATEGORIES:
				request = String.format(URL_CATEGORY, CATEGORIES_ID[item], page + 1);
				// parseParam1 = 1;
				maxSize = 57;
				break;
			case TAB_TOPS:
				// parseParam1 = 0;
				maxSize = MAX_SIZES[item];
				if (item == ITEM_TOP_DAY) {
					// parseParam2 = 0;
					request = String.format(URL_TOPS[item], date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
					// Log.d("#", request);
				} else {
					request = URL_TOPS[item];

				}

				break;
			case TAB_AUTHORS:
				break;

		}

		//Log.d("request=", request);

		return request;

	}


	private ArrayList<Map<Integer, String>> parseHtml(String html) {

		ArrayList<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
		Map<Integer, String> map;
		String link;
		String icon;
		String name;
		String date;
		String author;
		String award;

		// Document doc = Jsoup.parse(html);
		Element root = null;
		Elements e;

		Elements photolist;

		if (mDoc == null)
			return null;

		if (tab == TAB_TOPS) {
			// search ul.bigphotolist
			root = mDoc.select("div.photos-content.cols").first();

		} else {
			root = mDoc.select("div.photos-content.cols").first();
		}

		if (root == null) {
			L.e("root is null");
			return null;
		}

		photolist = root.children();
		L.v("photolist size=" + photolist.size());
		String replacement = App.isLowRes ? "_large." : "_xlarge.";
		for (Element photo : photolist) {
			map = new HashMap<>();
			Elements a_hrefs = photo.select("a[href]"); // это все ссылочные теги в фотке
			if (a_hrefs.size() == 0)
				continue;

			icon = a_hrefs.get(0).child(0).attr("src"); // ссылка на тхумб картинку
			link = a_hrefs.get(0).attr("href"); // ссылка на страницу с фото
			name = a_hrefs.get(0).child(0).attr("alt"); // имя фотки
			date = "Not supported yet";// photo.select("span.dateRaw").text(); //дата

			// award parsing
			award = "";
			/*
			 * e = photo.getElementsByAttributeValueStarting("src", "/images/icon/"); if (!e.isEmpty()) { award = e.get(0).attr("alt"); }
			 * else award = "";
			 */

			e = photo.getElementsByTag("thumbautor");
			if (!e.isEmpty()) {
				author = e.get(0).child(0).child(0).text();
			} else
				author = "";

			// if nude
			if (icon.contains("/skin/")) {
				icon = URL_MAIN + icon;
				mLargeImage = icon;
			} else {

				mLargeImage = icon.replace("_icon.", replacement)
				                  .replace("pv_", "")
				                  .replace("prv-", "img-")
				                  .replace("_thumb.", replacement);
			}
			L.d("links " + a_hrefs.size());
			L.d("link " + link);
			L.d("icon " + icon);
			L.d("name " + name);
			L.d("dateRaw " + date);
			L.d("author " + author);
			L.d("award " + award);

			map.put(DATA_IMG_NAME, name);
			map.put(DATA_URL_SMALL, icon);
			map.put(DATA_URL_LARGE, mLargeImage);
			map.put(DATA_URL_PAGE, URL_MAIN + link);
			map.put(DATA_DATE, date);
			map.put(DATA_AUTHOR, author);
			map.put(DATA_AWARD, award);
			list.add(map);

		}

		// Log.d("HERE!",""+photos.get(0).html());
		//L.v("size=" + list.size());

		return list;
	}


	public void setOnLoadListener(OnLoadListener l) {
		listener = l;
	}


	class GetHtmlTask extends AsyncTask<String, Integer, ArrayList<Map<Integer, String>>> {

		@Override
		protected ArrayList<Map<Integer, String>> doInBackground(String... req) {
			String html = "";
			// String html = downloadOld(req[0]);
			download(req[0]);
			ArrayList<Map<Integer, String>> urlList;
			urlList = parseHtml(html);
			return urlList;
		}


		@Override
		protected void onPostExecute(ArrayList<Map<Integer, String>> result) {
			listener.onLoad(result, page);

			super.onPostExecute(result);
		}


		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values[0] != -1)
				listener.onProgress(page, values[0]);
			else
				listener.onProgressEnd(page);
		}


		private void download(String urlString) {
			try {
				L.v("url=" + urlString);
				mDoc = Jsoup.connect(urlString)
				            .cookie("show_nude", isCensored ? "0" : "1")
				            .cookie("adult_mode", isCensored ? "0" : "1")
				            .get();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	public interface OnLoadListener {

		public void onLoad(ArrayList<Map<Integer, String>> result, int page);


		public void onProgress(int page, int progress);


		public void onProgressEnd(int page);
	}

}
