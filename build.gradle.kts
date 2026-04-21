plugins {
    kotlin("multiplatform") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

group = "com.connectfour"
version = "1.0.0"

kotlin {
    js(IR) {
        moduleName = "connect-four"
        browser {
            commonWebpackConfig {
                outputFileName = "connect-four.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.html.core)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
