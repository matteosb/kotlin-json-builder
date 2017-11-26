package codes.matteo.jsondsl

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test

class JsonDslTest {

    @Test
    fun buildAndPrintComplexObject() {
        val obj = jsObject {
            "metadata" /= jsObject {
                "version" /= 1
                "something_nested" /= jsObject {
                    "foo" /= jsObject {
                        "bar" /= Long.MAX_VALUE
                    }
                }
            }

            "object_array" /= arrayOf(jsObject { "a" /= 1 }, jsObject { "b" /= 1 })

            "string_array" /= arrayOf("a", "b", "c")
            "nested_jsarrays" /= arrayOf(jsArray { elem(1) }, jsArray { elem(2); elem(3) })
            "deeply_arrays_need" /= jsArray { elem(jsArray { elem(jsArray { elem(1) }) }) }
            "mixed_types" /= jsArray {
                elem("a")
                elem(2)
                elem(jsObject { "x" /= "y" })
            }
        }

        val om = ObjectMapper()
        val ser = om.writeValueAsString(obj.asTree())
        assertThat(ser, containsString("\"version\":1"))
        println(ser)
    }
}