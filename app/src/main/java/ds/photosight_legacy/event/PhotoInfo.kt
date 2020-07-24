package ds.photosight_legacy.event

public class PhotoInfo(
        public var tab: Int,
        public var category: Int,
        public var page: Int,
        public var item: Int,
        public var data: List<Map<Int, String>>? = null)
