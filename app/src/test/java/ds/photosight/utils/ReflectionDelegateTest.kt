package ds.photosight.utils

import org.junit.Test
import kotlin.test.assertEquals

class ReflectionDelegateTest {

    class Target {
        private var privateField: String = "123"

        fun getField(): String = privateField
    }

    private val target = Target()

    @Test
    fun `test read`() {
        val f by target.reflection<String>("privateField")
        assertEquals("123", f)
    }

    @Test
    fun `auto evaluate field name`() {
        val privateField by target.reflection<String>()
        assertEquals("123", privateField)
    }

    @Test
    fun `test write`() {
        var f by target.reflection<String>("privateField")
        f = "hello"
        assertEquals("hello", target.getField())
    }
}