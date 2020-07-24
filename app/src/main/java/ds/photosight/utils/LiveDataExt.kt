package ds.photosight.utils

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.hadilq.liveevent.LiveEvent
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

fun <T : Any> mutableLiveData(default: T) = MutableLiveDataDelegate(MutableLiveData(default))
fun <T : Any> mutableLiveData(liveData: MutableLiveData<T>) = MutableLiveDataDelegate(liveData)

fun <T : Any> liveData(liveData: LiveData<T>) = LiveDataDelegate(liveData)
fun <T : Any> liveData(default: T) = LiveDataDelegate(MutableLiveData(default))

fun <T> nullableLiveData(default: T?) = LiveDataDelegate(MutableLiveData(default))
fun <T> nullableLiveData(liveData: LiveData<T?>) = LiveDataDelegate(liveData)

fun <T> nullableMutableLiveData(default: T?) = MutableLiveDataDelegate(MutableLiveData(default))
fun <T> nullableMutableLiveData(liveData: MutableLiveData<T?>) = MutableLiveDataDelegate(liveData)

class MutableLiveDataDelegate<T>(val liveData: MutableLiveData<T>) : ReadWriteProperty<Any, T> {

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T = liveData.value as T

    override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            liveData.value = value
        } else {
            liveData.postValue(value)
        }
    }
}

class LiveDataDelegate<T>(val liveData: LiveData<T>) : ReadOnlyProperty<Any, T> {
    override operator fun getValue(thisRef: Any, property: KProperty<*>): T = liveData.value as T
}

@Suppress("UNCHECKED_CAST")
val <T : Any?> KProperty0<T>.liveData: LiveData<T>
    get() {
        isAccessible = true
        return when (val d = getDelegate()) {
            is LiveDataDelegate<*> -> d.liveData as LiveData<T>
            is MutableLiveDataDelegate<*> -> d.liveData as LiveData<T>
            else -> error("not supported")
        }
    }

fun <T : Any?> KProperty0<T>.observe(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    liveData.observe(owner, Observer { t -> onChanged.invoke(t) })
}

operator fun <T> LiveEvent<T>.invoke(value: T) {
    this.value = value
}

operator fun LiveEvent<Unit>.invoke() {
    this.value = Unit
}