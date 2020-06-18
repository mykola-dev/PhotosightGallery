package ds.photosight.parser

fun String.thumbToLarge(): String = this
    .replace("prv-", "img-")
    .replace("pv_", "")
    .replace("_icon.", "_xlarge.")
    .replace("_thumb.", "_xlarge.")

var debugEnabled = false