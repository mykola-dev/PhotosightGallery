package ds.photosight.parser

private val pattern = Regex("""https://cdn\.sight\.photo/p/(t).+?\d+\.jpg""")

fun String.thumbToLarge(): String =
    pattern.matchEntire(this)
        ?.let {
            val range = it.groups[1]!!.range
            this.replaceRange(range, "x")
        }
        ?: error("wrong url pattern")

var debugEnabled = false