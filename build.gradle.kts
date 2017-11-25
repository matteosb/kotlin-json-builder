import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "codes.matteo"
version = "1.0-SNAPSHOT"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.1.61"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
}

apply {
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jre8", kotlin_version))
    compile("com.fasterxml.jackson.core", "jackson-core", "2.9.2")
    compile("com.fasterxml.jackson.core", "jackson-databind", "2.9.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
