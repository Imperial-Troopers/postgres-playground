package com.github.imperial.troopers.postgres.playground

import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory.getLogger
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import javax.sql.DataSource

class PostgresContainer : PostgreSQLContainer<PostgresContainer>("postgres:13.4-alpine") {
    init {
        withLogConsumer(
            Slf4jLogConsumer(
                getLogger("PostgresContainer")
            )
        )
    }

    val dataSource: DataSource
        get() = PGSimpleDataSource().also {
            it.user = username
            it.password = password
            it.setURL(jdbcUrl)
        }
}
