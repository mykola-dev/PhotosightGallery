package ds.photosight.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Handler;
import ds.photosight.utils.L;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AdvancedInfoParser {

	public static enum Statuses {
		pretender, lover, pro, master, guru
	}


	private static final String ORIG_DATE_FORMAT = "dd MMMM yyyy, kk:mm:ss";
	private static final String DEST_DATE_FORMAT = "kk:mm dd.MM.yy";
	private static final String[] VOTE_CLASSES = {
			".item.item-x",
			".item.item-o",
			".item.item-t",
			".item.item-up",
			".item.item-down",
	};

	private List<Comment> mComments;
	private List<Integer> mRates;
	private List<String> mAvards;


	public void parseAsync(final String url, final Callback c) {
		final Handler h = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (c != null)
					try {
						parse(url);
						h.post(new Runnable() {
							@Override
							public void run() {
								c.onDone(mComments, mRates, mAvards);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				else
					L.e("callback is null!");
			}
		}).start();
	}


	public void parse(String url) throws IOException {
		mComments = new ArrayList<>();
		mRates = new ArrayList<>();
		mAvards = new ArrayList<>();

		SimpleDateFormat df = new SimpleDateFormat(ORIG_DATE_FORMAT, new Locale("ru"));
		SimpleDateFormat df2 = new SimpleDateFormat(DEST_DATE_FORMAT);
		Document doc = Jsoup.connect(url).get();

		// rates
		Elements photoInfo = doc.select(".photo-info");
		for (String cls : VOTE_CLASSES) {
			mRates.add(Integer.valueOf(photoInfo.select(cls).select(".count").text()));
		}
		L.v("rates fetched %s",mRates.size());

		// avard
		Elements tops = doc.select(".medels.glued");
		for (Element e : tops) { mAvards.add(e.attr("src")); }
		L.v("awards fetched %s",mAvards.size());

		// comments
		Elements comments = doc.select("div.comments div.comment-content");
		if (comments!=null) {
			for (Element e : comments) {
				Comment comment = new Comment();
				comment.content = e.getElementsByTag("p").first().text();
				comment.avatarUrl = e.getElementsByTag("img").attr("src");
				comment.rate = e.select("span.count").text();
				comment.name = e.select("a.name").text();

				//comment.status = status.className();

				comment.dateRaw = e.select("span.date").text();
				try {
					comment.date = df.parse(comment.dateRaw);
					comment.dateFormatted = df2.format(comment.date);
				} catch (ParseException e1) {
					e1.printStackTrace();

				}
				mComments.add(comment);
			}
		}
		L.v("comments fetched %s",mComments.size());

		// log
		for (Comment c : mComments) {
			L.v("================================");
			L.v("content=" + c.content);
			L.v("name=" + c.name);
			L.v("dateRaw=" + c.dateRaw);
			L.v("date=" + c.dateFormatted);
			L.v("rating=" + c.rate);
			L.v("avatar=" + c.avatarUrl);
			L.v("status=" + c.status);

		}
		StringBuilder sVotes = new StringBuilder();
		for (int rate : mRates) { sVotes.append(rate).append(" "); }
		L.v("votes: " + sVotes.toString());
		//L.v("avard: " + mAvard);


	}


	public interface Callback {

		public void onDone(List<Comment> comments, List<Integer> rates, List<String> avards);
	}
}
