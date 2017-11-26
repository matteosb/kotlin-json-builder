package codes.matteo.jsondsl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

sealed class JsonBuilderNode {
    abstract fun asTree(): JsonNode

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is JsonBuilderNode -> asTree() == other.asTree()
            else -> false
        }
    }

    override fun toString(): String = asTree().toString()

    override fun hashCode(): Int = asTree().hashCode()
}

class JsonBuilderArray : JsonBuilderNode() {
    private val node = JsonNodeFactory.instance.arrayNode()

    fun elem(v: String) = node.add(v)

    fun elem(v: Int) = node.add(v)

    fun elem(v: Long) = node.add(v)

    fun elem(v: JsonBuilderNode) = node.add(v.asTree())

    override fun asTree(): JsonNode = node
}

class JsonBuilderObject : JsonBuilderNode() {
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

    operator fun String.divAssign(v: JsonBuilderNode) {
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

    operator fun String.divAssign(v: Array<JsonBuilderNode>) {
        val arr = node.putArray(this)
        v.forEach { arr.add(it.asTree()) }
    }
}

fun jsObject(init: JsonBuilderObject.() -> Unit): JsonBuilderNode {
    val obj = JsonBuilderObject()
    obj.init()
    return obj
}

fun jsArray(init: JsonBuilderArray.() -> Unit): JsonBuilderNode {
    val arr = JsonBuilderArray()
    arr.init()
    return arr
}