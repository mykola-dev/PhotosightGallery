package ds.photosight.utils

import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> Any.reflection(fieldName: String): ReflectionDelegate<T> = ReflectionDelegate(this, fieldName)
fun <T> Any.reflection() = ReflectionDelegateProvider<T>(this)

class ReflectionDelegateProvider<T>(private val target: Any) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReflectionDelegate<T> = ReflectionDelegate(target, prop.name)
}

@Suppress("UNCHECKED_CAST")
class ReflectionDelegate<T>(private val target: Any, fieldName: String) : ReadWriteProperty<Any?, T> {
    private val field = target.javaClass.findFieldRecursively(fieldName)

    private fun Class<*>.findFieldRecursively(fieldName: String): Field = try {
        getDeclaredField(fieldName)
    } catch (e: NoSuchFieldException) {
        superclass?.findFieldRecursively(fieldName) ?: throw e
    }
        .apply { isAccessible = true }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = field.get(target) as T

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        field.set(target, value)
    }
}