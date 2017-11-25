package codes.matteo.jsondsl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.JsonNodeFactory

sealed class JsonDslNode {
    abstract fun asTree(): JsonNode

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is JsonDslNode -> asTree() == other.asTree()
            else -> false
        }
    }

    override fun toString(): String = asTree().toString()

    override fun hashCode(): Int = asTree().hashCode()
}

class JsonDslArray : JsonDslNode() {
    private val node = JsonNodeFactory.instance.arrayNode()

    fun elem(v: String) = node.add(v)

    fun elem(v: Int) = node.add(v)

    fun elem(v: Long) = node.add(v)

    fun elem(v: JsonDslNode) = node.add(v.asTree())

    override fun asTree(): JsonNode = node
}

class JsonDslObject : JsonDslNode() {
    private val node = JsonNodeFactory.instance.objectNode()

    override fun asTree(): JsonNode = node

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
        node.set(this, v.asTree())
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
        v.forEach { arr.add(it.asTree()) }
    }
}

fun jsObject(init: JsonDslObject.() -> Unit): JsonDslNode {
    val obj = JsonDslObject()
    obj.init()
    return obj
}

fun jsArray(init: JsonDslArray.() -> Unit): JsonDslNode {
    val arr = JsonDslArray()
    arr.init()
    return arr
}

fun main(args: Array<String>) {
    val obj = jsObject {
        "metadata" /= jsObject {
            "version" /= "1"
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
    om.enable(SerializationFeature.INDENT_OUTPUT)
    println(om.writeValueAsString(obj.asTree()))
}