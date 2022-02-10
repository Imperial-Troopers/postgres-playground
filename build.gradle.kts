import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("com.revolut.jooq-docker") version "0.3.7"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

jooq {
    image {
        tag = "13.4-alpine"
    }
}

tasks {
    generateJooqClasses {
        basePackageName = "com.github.imperial.troopers.postgres.playground.jooq"
        excludeFlywayTable = true
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    withType<Test> {
        useJUnitPlatform()
        systemProperty("org.jooq.no-logo", "true")
        systemProperty("org.jooq.no-tips", "true")
        testLogging {
            events = setOf(PASSED, SKIPPED, FAILED)
            exceptionFormat = FULL
        }
    }
}

dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    jdbc("org.postgresql:postgresql")
    implementation("org.postgresql:postgresql")
    implementation("org.jooq:jooq")
    implementation("org.flywaydb:flyway-core:8.4.4")

    testImplementation("io.strikt:strikt-jvm:0.34.1")
    testImplementation(enforcedPlatform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
