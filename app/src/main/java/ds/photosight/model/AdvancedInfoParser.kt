package ds.photosight.model

import android.os.Handler
import ds.photosight.utils.L
import org.jsoup.Jsoup
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale
import kotlin.properties.Delegates

public class AdvancedInfoParser {


    private val mComments: MutableList<Comment> by Delegates.lazy { ArrayList<Comment>() }
    private val rates: MutableList<Int>  by Delegates.lazy { ArrayList<Int>() }
    private val avards: MutableList<String>  by Delegates.lazy { ArrayList<String>() }

    private val ORIG_DATE_FORMAT = "dd MMMM yyyy, kk:mm:ss"
    private val DEST_DATE_FORMAT = "kk:mm dd.MM.yy"
    private val VOTE_CLASSES = array(".item.item-x", ".item.item-o", ".item.item-t", ".item.item-up", ".item.item-down")


    public fun parseAsync(url: String, c: Callback) {
        val h = Handler()
        Thread(object : Runnable {
            override fun run() {
                    try {
                        parse(url)
                        h.post(object : Runnable {
                            override fun run() {
                                c.onDone(mComments, rates, avards)
                            }
                        })
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
            }
        }).start()
    }


    public fun parse(url: String) {
        L.v("url=$url")
        mComments.clear()
        rates.clear()
        avards.clear()

        val df = SimpleDateFormat(ORIG_DATE_FORMAT, Locale("ru"))
        val df2 = SimpleDateFormat(DEST_DATE_FORMAT)
        val doc = Jsoup.connect(url).get()

        // rates
        val photoInfo = doc.select(".photo-info")
        for (cls in VOTE_CLASSES) {
            rates.add(Integer.valueOf(photoInfo.select(cls).select(".count").text()))
        }
        L.v("rates fetched %s", rates.size())

        // avard
        val tops = doc.select(".medels.glued")
        for (e in tops) {
            avards.add(e.attr("src"))
        }
        L.v("awards fetched %s", avards.size())

        // comments
        val comments = doc.select("div.comments div.comment-content")
        if (comments != null) {
            for (e in comments) {
                val comment = Comment()
                comment.content = e.getElementsByTag("p").first().text()
                comment.avatarUrl = e.getElementsByTag("img").attr("src")
                comment.rate = e.select("span.count").text()
                comment.name = e.select("a.name").text()

                //comment.status = status.className();

                comment.dateRaw = e.select("span.date").text()
                try {
                    comment.date = df.parse(comment.dateRaw)
                    comment.dateFormatted = df2.format(comment.date)
                } catch (e1: ParseException) {
                    e1.printStackTrace()

                }

                mComments.add(comment)
            }
        }
        L.v("comments fetched %s", mComments.size())

        // log
        for (c in mComments) {
            L.v("================================")
            L.v("content=" + c.content)
            L.v("name=" + c.name)
            L.v("dateRaw=" + c.dateRaw)
            L.v("date=" + c.dateFormatted)
            L.v("rating=" + c.rate)
            L.v("avatar=" + c.avatarUrl)
            L.v("status=" + c.status)

        }
        val sVotes = StringBuilder()
        for (rate in rates) {
            sVotes.append(rate).append(" ")
        }
        L.v("votes: " + sVotes.toString())
        //L.v("avard: " + mAvard);


    }


    public interface Callback {

        public fun onDone(comments: List<Comment>, rates: List<Int>, avards: List<String>)
    }

}
