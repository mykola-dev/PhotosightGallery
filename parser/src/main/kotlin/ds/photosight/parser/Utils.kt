package ds.photosight.parser

val pattern2020 = Regex("""https://cdny\.de/\w+/\w+(/.+?).jpg""")

fun String.thumbToLarge(): String = if (pattern2020.matches(this)) {
    pattern2020.replace(this, "https://cdn.photosight.ru/img\$1_xlarge.jpg")
} else {
    this
        .replace("prv-", "img-")
        .replace("pv_", "")
        .replace("_icon.", "_xlarge.")
        .replace("_thumb.", "_xlarge.")
}

var debugEnabled = false