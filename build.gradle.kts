import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21" apply false
    kotlin("plugin.spring") version "2.2.21" apply false
    id("org.springframework.boot") version "4.0.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.corebank"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    configure<org.gradle.api.plugins.JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    dependencies {
        add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
        add("implementation", "tools.jackson.module:jackson-module-kotlin")
        add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        add("implementation", "io.projectreactor.kotlin:reactor-kotlin-extensions")
        
        add("testImplementation", "org.jetbrains.kotlin:kotlin-test-junit5")
        add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        
        // Kotest and MockK as specified
        add("testImplementation", "io.kotest:kotest-runner-junit5:5.7.0")
        add("testImplementation", "io.kotest:kotest-assertions-core:5.7.0")
        add("testImplementation", "io.mockk:mockk:1.13.5")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
