package ds.photosight.utils

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <V> savedState(): ReadWriteProperty<SavedStateHandle, V?> = object : ReadWriteProperty<SavedStateHandle, V?> {
    override fun setValue(thisRef: SavedStateHandle, property: KProperty<*>, value: V?) {
        thisRef.set(property.name, value)
    }

    override fun getValue(thisRef: SavedStateHandle, property: KProperty<*>): V? = thisRef.get(property.name)
}