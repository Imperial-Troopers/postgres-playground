package com.github.imperial.troopers.postgres.playground

import com.github.imperial.troopers.postgres.playground.jooq.tables.records.TestRecord
import org.jooq.DSLContext
import kotlin.random.Random

object TestRecordFixtures {
    fun DSLContext.generateRecords(number: Int, recordsPerGeozone: Int = 1, valueSupplier: () -> String?) {
        generateSequence(0, Int::inc)
            .take(number)
            .flatMap { index ->
                val geozoneCode = "${Random.latinString()}$index"
                (1..recordsPerGeozone).map { recordIndex ->
                    TestRecord(
                        geozoneCode,
                        "${Random.latinString()}$index$recordIndex",
                        valueSupplier()
                    )
                }
            }
            .chunked(100)
            .forEach { records ->
                batchInsert(records).execute()
            }
    }
}
