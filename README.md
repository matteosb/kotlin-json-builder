# kotlin-json-builder

A reasonably fast, reasonably elegant Kotlin typesafe builder for Jackson JSON objects.

## Usage

The DSL provides two functions: `jsObject` and `jsArray`. In addition to `jsObject` and `jsArray`, basic Kotlin types are
supported: `Boolean`, `String`, `Int`, `Long` and Kotlin `Array`s of those types.
The DSL errs on the side of typesafety and doesn't allow potentially un-serializable objects to be created. Using `Array`
is preferable over `jsArray` unless you need deeply nested arrays or mixed type arrays.

* `jsObject` provides the `/=` operator to associate string keys to values
* `jsArray` provides the `elem` method to add elements. 

That's it! Here is an example:

```kotlin
val obj = jsObject {
        // nested object
        "data" /= jsObject {
            "id" /= 1
            "name" /= "Joe"
            // native array
            "tags" /= arrayOf("a", "b")
            // jsArray
            "nested_array" /= jsArray { elem(jsArray { elem(1) }) }
        }
      }
 
val arr = jsArray {
  elem(1)
  elem("something")
  elem(obj)
} 

// call `asTree()` to get the JsonNode representation
// a trivial jackson module could be added in the future to us to skip this
val serialized = ObjectMapper().writeValueAsString(obj.asTree())
```


## Benchmarks

This project includes basic JMH benchmarks for serialization.
These compare using the DSL to data class databinding and various methods of
constructing dynamic JSON objects. The aim is to ensure that DSL performance stays in
the same ballpark as databinding and hand-written Jackson ObjectNode code. 


To run benchmarks:
```bash
./gradlew clean jmh

# if you get errors, you might need to remove the gradle cache:
# rm -r ~/.gradle
```

Latest results
```
Benchmark                    Mode  Cnt        Score       Error  Units
BenchKotlin.dataClass       thrpt   20  2532032.245 ± 34718.349  ops/s
BenchKotlin.dsl             thrpt   20  1514419.037 ± 26120.672  ops/s
BenchKotlin.handWritten     thrpt   20  1560949.008 ± 12864.029  ops/s
BenchKotlin.mapOf           thrpt   20  1413689.265 ± 10756.033  ops/s
BenchKotlin.stringInterpol  thrpt   20   106053.933 ±   499.961  ops/s
```
