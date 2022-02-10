package com.github.imperial.troopers.postgres.playground

import kotlin.random.Random
import kotlin.random.nextInt

fun Random.latinString() = string('A'..'Z')
fun Random.cyrillicString() = string('А'..'Я')

fun Random.string(charset: CharRange, size: IntRange = 10..35): String {
    val stringBuilder = StringBuilder()
    val actualSize = nextInt(size)
    repeat(actualSize) { _ ->
        stringBuilder.append(charset.random().randomCase())
    }

    return stringBuilder.toString()
}

private fun Char.randomCase() = takeIf { Random.nextBoolean() } ?: lowercaseChar()
private fun CharRange.random() = Random.nextInt(first.code..last.code).let(::Char)
