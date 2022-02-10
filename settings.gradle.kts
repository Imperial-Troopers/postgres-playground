rootProject.name = "postgres-playground"

val postgresJdbcVersion = "42.3.2"
val jooqVersion = "3.16.3"

gradle.allprojects {
    buildscript {
        configurations.configureEach {
            resolutionStrategy {
                eachDependency {
                    when(requested.group) {
                        "org.jooq" -> useVersion(jooqVersion)
                    }
                }
            }
        }
    }

    configurations.configureEach {
        resolutionStrategy {
            force("org.postgresql:postgresql:$postgresJdbcVersion")

            eachDependency {
                when (requested.group) {
                    "org.jooq" -> useVersion(jooqVersion)
                }
            }
        }
    }
}
