package com.github.imperial.troopers.postgres.playground

import com.github.imperial.troopers.postgres.playground.TestRecordFixtures.generateRecords
import com.github.imperial.troopers.postgres.playground.jooq.tables.Test.TEST
import com.github.imperial.troopers.postgres.playground.jooq.tables.records.TestRecord
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect.POSTGRES
import org.jooq.impl.DSL
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import strikt.api.expectThat
import kotlin.random.Random

@TestInstance(Lifecycle.PER_CLASS)
class CyrillicVsLatinKeyTest {

    private lateinit var jooq: DSLContext

    @BeforeAll
    fun dbInit() {
        postgresContainer.start()
        Flyway.configure()
            .dataSource(postgresContainer.dataSource)
            .load()
            .migrate()
        jooq = DSL.using(
            postgresContainer.dataSource,
            POSTGRES
        )
        jooq.generateData()
    }

    @AfterAll
    fun dbShutDown() {
        postgresContainer.stop()
    }

    @Test
    fun `should use index scan when arguments are latin`() {
        // given
        val (latinRecord1, latinRecord2) = jooq.selectWithValueMatching("[A-Za-z]+")

        // when
        val explanaiton = jooq.explainWithLogging(
            jooq.dumbQuery(latinRecord1, latinRecord2)
        )

        // then
        expectThat(explanaiton) {
            `does not have sequential scans`()
        }
    }

    @Test
    fun `should not use index scan when arguments are cyrillic`() {
        // given
        val (cyrillicRecord1, cyrillicRecord2) = jooq.selectWithValueMatching("[А-Яа-я]+")

        // when
        val explanaiton = jooq.explainWithLogging(
            jooq.dumbQuery(cyrillicRecord1, cyrillicRecord2)
        )

        // then
        expectThat(explanaiton) not {
            `does not have sequential scans`()
        }
    }

    private fun DSLContext.dumbQuery(conditionMatcher1: TestRecord, conditionMatcher2: TestRecord) = jooq.query(
        // language=PostgreSQL
        """
            SELECT test.geozone_code FROM test WHERE
            test.code = '${conditionMatcher1.code}' AND test.value = '${conditionMatcher1.value}'
            OR test.code = '${conditionMatcher2.code}' AND test.value = '${conditionMatcher2.value}'
            group by test.geozone_code
            having SUM(1) = 2

        """.trimIndent()
    )

    private fun DSLContext.selectWithValueMatching(regex: String) = selectFrom(TEST)
        .where(TEST.VALUE.likeRegex(regex))
        .limit(2)
        .fetchArray()

    private fun DSLContext.generateData() {
        generateRecords(number = 50, recordsPerGeozone = 1, valueSupplier = Random::latinString)
        generateRecords(number = 25, recordsPerGeozone = 2, valueSupplier = Random::latinString)
        generateRecords(number = 80, recordsPerGeozone = 2, valueSupplier = Random::cyrillicString)
        generateRecords(number = 40, recordsPerGeozone = 4, valueSupplier = Random::cyrillicString)
        generateRecords(number = 10, valueSupplier = { null })
    }

    private companion object {
        val postgresContainer = PostgresContainer()
    }
}
