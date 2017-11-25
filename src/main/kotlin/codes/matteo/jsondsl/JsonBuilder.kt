package codes.matteo.jsondsl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.JsonNodeFactory

sealed class JsonDslNode {
    abstract fun toJacksonNode(): JsonNode

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is JsonDslNode -> toJacksonNode() == other.toJacksonNode()
            else -> false
        }
    }

    override fun toString(): String = toJacksonNode().toString()

    override fun hashCode(): Int = toJacksonNode().hashCode()
}

class JsonArray : JsonDslNode() {
    private val node = JsonNodeFactory.instance.arrayNode()

    fun elem(v: String) = node.add(v)

    fun elem(v: Int) = node.add(v)

    fun elem(v: Long) = node.add(v)

    fun elem(v: JsonDslNode) = node.add(v.toJacksonNode())

    override fun toJacksonNode(): JsonNode = node
}

class JsonObject : JsonDslNode() {
    private val node = JsonNodeFactory.instance.objectNode()

    override fun toJacksonNode(): JsonNode = node

    operator fun String.divAssign(v: String) {
        node.put(this, v)
    }

    operator fun String.divAssign(v: Int) {
        node.put(this, v)
    }

    operator fun String.divAssign(v: Long) {
        node.put(this, v)
    }

    operator fun String.divAssign(v: JsonDslNode) {
        node.set(this, v.toJacksonNode())
    }

    operator fun String.divAssign(v: Array<String>) {
        val arr = node.putArray(this)
        v.forEach { arr.add(it) }
    }

    operator fun String.divAssign(v: Array<Int>) {
        val arr = node.putArray(this)
        v.forEach { arr.add(it) }
    }

    operator fun String.divAssign(v: Array<Long>) {
        val arr = node.putArray(this)
        v.forEach { arr.add(it) }
    }

    operator fun String.divAssign(v: Array<JsonDslNode>) {
        val arr = node.putArray(this)
        v.forEach { arr.add(it.toJacksonNode()) }
    }
}

fun jsObj(init: JsonObject.() -> Unit): JsonDslNode {
    val obj = JsonObject()
    obj.init()
    return obj
}

fun jsArray(init: JsonArray.() -> Unit): JsonDslNode {
    val arr = JsonArray()
    arr.init()
    return arr
}

fun main(args: Array<String>) {
    val obj = jsObj {
        "metadata" /= jsObj {
            "version" /= "1"
            "something_nested" /= jsObj {
                "foo" /= jsObj {
                    "bar" /= Long.MAX_VALUE
                }
            }
        }

        "object_array" /= arrayOf(jsObj { "a" /= 1 }, jsObj { "b" /= 1 })

        "string_array" /= arrayOf("a", "b", "c")
        "nested_jsarrays" /= arrayOf(jsArray { elem(1) }, jsArray { elem(2); elem(3) })
        "deeply_arrays_need" /= jsArray { elem(jsArray { elem(jsArray { elem(1) }) }) }
        "mixed_types" /= jsArray {
            elem("a")
            elem(2)
            elem(jsObj { "x" /= "y" })
        }
    }

    val om = ObjectMapper()
    om.enable(SerializationFeature.INDENT_OUTPUT)
    println(om.writeValueAsString(obj.toJacksonNode()))
}