plugins {
    kotlin("multiplatform") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
    application
}

group = "com.hacktopiealgoridom.postgresql.fullstack"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        val jvmVersion: String by project
        jvmToolchain(Integer.parseInt(jvmVersion))
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                // testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
                // testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
            }
        }
        val jvmMain by getting {

            val exposedVersion: String by project
            val ktorVersion: String by project
            val kotlinxVersion: String by project
            val postgresqlVersion: String by project
            val hikariVersion: String by project
            val log4jVersion: String by project
            val swaggerCodegenVersion: String by project

            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                // implementation("io.ktor:ktor-jackson:$ktorVersion")
                // implementation("io.ktor:ktor-features:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxVersion")
                implementation("org.postgresql:postgresql:$postgresqlVersion")

                implementation ("com.zaxxer:HikariCP:$hikariVersion")

                implementation ("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation ("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
                implementation ("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation ("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

                // implementation ("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
                // implementation ("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
                implementation ("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
                implementation ("org.jetbrains.exposed:exposed-json:$exposedVersion")
                // implementation ("org.jetbrains.exposed:exposed-money:$exposedVersion")

                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

                implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

                implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

                implementation("io.swagger.codegen.v3:swagger-codegen-generators:$swaggerCodegenVersion")

                implementation("io.ktor:ktor-server-swagger:$ktorVersion")
                implementation("io.ktor:ktor-server-openapi:$ktorVersion")
                implementation("io.ktor:ktor-server-cors:$ktorVersion")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.3.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.1.2-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.6-pre.346")

                implementation(npm("react-quill", "1.3.5"))
                implementation(npm("axios", "0.21.1"))
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("com.hacktopiealgoridom.postgresql.fullstack.application.ServerKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}
