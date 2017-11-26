# kotlin-json-builder
A reasonable fast Kotlin typesafe builder for Jacskon JSON objects

# Benchmarks


Benchmark                    Mode  Cnt        Score       Error  Units
BenchKotlin.dataClass       thrpt   20  2532032.245 ± 34718.349  ops/s
BenchKotlin.dsl             thrpt   20  1514419.037 ± 26120.672  ops/s
BenchKotlin.handWritten     thrpt   20  1560949.008 ± 12864.029  ops/s
BenchKotlin.mapOf           thrpt   20  1413689.265 ± 10756.033  ops/s
BenchKotlin.stringInterpol  thrpt   20   106053.933 ±   499.961  ops/s
