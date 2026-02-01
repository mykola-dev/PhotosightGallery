package ds.photosight.shared.ui.model

import org.jetbrains.compose.resources.StringResource
import photosight.shared.generated.resources.Res
import photosight.shared.generated.resources.category_10
import photosight.shared.generated.resources.category_11
import photosight.shared.generated.resources.category_12
import photosight.shared.generated.resources.category_13
import photosight.shared.generated.resources.category_14
import photosight.shared.generated.resources.category_15
import photosight.shared.generated.resources.category_16
import photosight.shared.generated.resources.category_17
import photosight.shared.generated.resources.category_18
import photosight.shared.generated.resources.category_19
import photosight.shared.generated.resources.category_2
import photosight.shared.generated.resources.category_27
import photosight.shared.generated.resources.category_3
import photosight.shared.generated.resources.category_36
import photosight.shared.generated.resources.category_4
import photosight.shared.generated.resources.category_5
import photosight.shared.generated.resources.category_6
import photosight.shared.generated.resources.category_64
import photosight.shared.generated.resources.category_65
import photosight.shared.generated.resources.category_7
import photosight.shared.generated.resources.category_70
import photosight.shared.generated.resources.category_8
import photosight.shared.generated.resources.category_80
import photosight.shared.generated.resources.category_82
import photosight.shared.generated.resources.category_87
import photosight.shared.generated.resources.category_9
import photosight.shared.generated.resources.category_91
import photosight.shared.generated.resources.category_92
import photosight.shared.generated.resources.category_94
import photosight.shared.generated.resources.category_96


enum class Category(val id: Int, val titleRes: StringResource) {
    PORTRAIT(2, Res.string.category_2),
    NATURE(3, Res.string.category_3),
    STILL_LIFE(4, Res.string.category_4),
    MACRO(5, Res.string.category_5),
    TRAVELS(6, Res.string.category_6),
    URBAN(7, Res.string.category_7),
    ANIMALS(8, Res.string.category_8),
    REPORTAGE(9, Res.string.category_9),
    SPORT(10, Res.string.category_10),
    HUMOR(11, Res.string.category_11),
    GENRE(12, Res.string.category_12),
    MUSEUM(13, Res.string.category_13),
    DIGITAL_ART(14, Res.string.category_14),
    NU(15, Res.string.category_15),
    OTHER(16, Res.string.category_16),
    PHOTOSIGHT(17, Res.string.category_17),
    GLAMOUR(18, Res.string.category_18),
    ADVERTISING(19, Res.string.category_19),
    PAPARAZZI(27, Res.string.category_27),
    LANDSCAPE(36, Res.string.category_36),
    PHOTO_HUNT(64, Res.string.category_64),
    UNDERSEA_WORLD(65, Res.string.category_65),
    GENRE_PORTRAIT(70, Res.string.category_70),
    KIDS_WORLD(80, Res.string.category_80),
    ARCHITECTURE_AND_INTERIOR(82, Res.string.category_82),
    PHOTO_MODELS(87, Res.string.category_87),
    WEDDING(91, Res.string.category_91),
    TECHNO(92, Res.string.category_92),
    MOBILE_PHOTO(94, Res.string.category_94),
    RESTORATION(96, Res.string.category_96);

    companion object {
        fun fromId(id: Int): Category? = entries.find { it.id == id }
    }
}
