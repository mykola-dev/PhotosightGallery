package ds.photosight.parser

val pattern2022 = Regex("""https://cdny\.de/p/(t).+?\d+.jpg""")

fun String.thumbToLarge(): String =
    pattern2022.matchEntire(this)
        ?.let {
            val range = it.groups[1]!!.range
            this.replaceRange(range, "x")
        }
        ?: error("wrong url pattern")

var debugEnabled = false