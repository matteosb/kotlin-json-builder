package codes.matteo.benchmark

import codes.matteo.jsondsl.jsObject
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

val fistNames = arrayOf("Joan", "James", "John", "Judy", "Jean", "Xavier")
val lastNames = arrayOf("Dench", "Dame", "Douglass", "DeSantis", "David", "Jones")
val ages = arrayOf(20, 21, 22, 23, 24, 25)
val objectMapper = ObjectMapper().registerModule(KotlinModule())

data class Tag(val name: String)
data class Meta(val version: Int, val tags: Array<Tag>)
data class Person(val fistName: String, val lastName: String, val age: Int)
data class Wrapper(val metadata: Meta, val data: Person)

open class BenchKotlin {

    @Benchmark
    fun stringInterpol(blackhole: Blackhole) {
        val doc = """{
              "metadata": {
                "version": 1,
                "tags": [
                  { "name": "foo" },
                  { "name": "bar" }
                ]
              },
              "data": {
                "age": ${0},
                "first_name": "${fistNames[0]}",
                "last_name": "${lastNames[0]}"
              }
            }""".trimIndent()
        blackhole.consume(doc.toByteArray(Charsets.UTF_8))
    }

    @Benchmark
    fun mapOf(blackhole: Blackhole) {
        val doc = mapOf(
                "metadata" to mapOf(
                        "version" to 1,
                        "tags" to arrayOf(
                                mapOf("name" to "foo"),
                                mapOf("name" to "bar")
                        )
                ),
                "data" to mapOf(
                        "age" to 0,
                        "first_name" to fistNames[0],
                        "last_name" to lastNames[0]
                )
        )

        blackhole.consume(objectMapper.writeValueAsBytes(doc))
    }

    @Benchmark
    fun handWritten(blackhole: Blackhole) {
        val nf = objectMapper.nodeFactory
        val root = nf.objectNode()
        val meta = root.putObject("metadata")
                .put("version", 1)
        meta.putArray("tags")
                .add(nf.objectNode().put("name", "foo"))
                .add(nf.objectNode().put("name", "bar"))
        root.putObject("data").put("age", 0)
                .put("first_name", fistNames[0])
                .put("last_name", lastNames[0])
        blackhole.consume(objectMapper.writeValueAsBytes(root))
    }

    @Benchmark
    fun dataClass(blackhole: Blackhole) {
        val doc = Wrapper(
                Meta(1, arrayOf(Tag("foo"), Tag("bar"))),
                Person(fistNames[0], lastNames[0], 0)
        )
        blackhole.consume(objectMapper.writeValueAsBytes(doc))
    }

    @Benchmark
    fun dsl(blackhole: Blackhole) {
        val doc = jsObject {
            "metadata" /= jsObject {
                "version" /= 1
                "tags" /= arrayOf(
                        jsObject { "name" /= "foo" },
                        jsObject { "name" /= "bar" }
                )
            }
            "data" /= jsObject {
                "age" /= 0
                "fist_name" /= fistNames[0]
                "last_name" /= lastNames[0]
            }
        }

        blackhole.consume(objectMapper.writeValueAsBytes(doc.asTree()))
    }
}