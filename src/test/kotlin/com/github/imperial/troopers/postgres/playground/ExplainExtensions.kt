package com.github.imperial.troopers.postgres.playground

import org.jooq.DSLContext
import org.jooq.Explain
import org.jooq.Query
import org.slf4j.LoggerFactory
import strikt.api.Assertion
import strikt.assertions.isEmpty
import java.util.regex.MatchResult

private val SEQ_SCAN_PATTERN = ".*Seq Scan.*".toPattern()
private val logger = LoggerFactory.getLogger("QueryPlanLogger")

fun Assertion.Builder<Explain>.`does not have sequential scans`() = apply {
    get { plan().sequentialScans }.isEmpty()
}

private val String.sequentialScans: List<String>
    get() = SEQ_SCAN_PATTERN.matcher(this)
        .results()
        .map(MatchResult::group)
        .toList()

fun DSLContext.explainWithLogging(query: Query): Explain {
    logger.info("Parameterized query:\n{}", query.sql)
    logger.info("Inlined query:\n{}", query)
    return explain(query).also {
        logger.info("Execution plan:\n{}", it.plan())
    }
}
