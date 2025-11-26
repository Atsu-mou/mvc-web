val kotlin_version: String by project
val logback_version: String by project
val h2_version: String by project
val exposed_version: String by project
val ktor_version = "3.3.2" // keep in sync with the plugin

plugins {
    kotlin("jvm") version "2.2.21"
    id("io.ktor.plugin") version "3.3.1" // or 3.3.0
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.MVCApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    // remove if you don't actually need EAP features
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Align all Ktor modules to the same version
    implementation(platform("io.ktor:ktor-bom:$ktor_version"))
    // Add inside the `dependencies` block in `build.gradle.kts`
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.6.0")

    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-config-yaml-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-freemarker-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    // kotlin
    implementation("io.insert-koin:koin-ktor:3.4.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.4.0")

    // DB / Exposed / H2
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")

    implementation("software.amazon.awssdk:dynamodb:2.20.0")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.20.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Tests
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation(platform("io.ktor:ktor-bom:$ktor_version"))
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation(kotlin("test")) // resolves to org.jetbrains.kotlin:kotlin-test
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    // Enable JUnit Platform (works with kotlin("test"))
    useJUnitPlatform()
}
