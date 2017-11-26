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
        for (i in 0..5) {
            val doc = """{
              "metadata": {
                "version": 1,
                "tags": [
                  { "name": "foo" },
                  { "name": "bar" }
                ]
              },
              "data": {
                "age": $i,
                "first_name": "${fistNames[i]}",
                "last_name": "${lastNames[i]}"
              }
            }""".trimIndent()
            blackhole.consume(doc.toByteArray(Charsets.UTF_8))
        }
    }

    @Benchmark
    fun dataClass(blackhole: Blackhole) {
        for (i in 0..5) {
            val doc = Wrapper(
                    Meta(1, arrayOf(Tag("foo"), Tag("bar"))),
                    Person(fistNames[i], lastNames[i], i)
            )
            blackhole.consume(objectMapper.writeValueAsBytes(doc))
        }
    }

    @Benchmark
    fun dsl(blackhole: Blackhole) {
        for (i in 0..5) {
            val doc = jsObject {
                "metadata" /= jsObject {
                    "version" /= 1
                    "tags" /= arrayOf(
                            jsObject { "name" /= "foo" },
                            jsObject { "name" /= "bar" }
                    )
                }
                "data" /= jsObject {
                    "age" /= i
                    "fist_name" /= fistNames[i]
                    "last_name" /= lastNames[i]
                }
            }

            blackhole.consume(objectMapper.writeValueAsBytes(doc.asTree()))
        }
    }
}