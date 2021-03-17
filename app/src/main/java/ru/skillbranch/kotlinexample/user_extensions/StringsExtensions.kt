package ru.skillbranch.kotlinexample.user_extensions

import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern

fun String.fullNameToPair() : Pair<String, String?> {
    return split(" ")
        .filter { it.isNotBlank() }
        .run {

            when (size) {
                1 -> first() to null
                2 -> first() to last()
                else -> {
                    throw IllegalArgumentException("Fullname must be contain firstname and lastname, current result ${this}")
                }
            }

        }
}

fun String.md5() : String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(toByteArray())
    val hexString = BigInteger(1, digest).toString(16)
    return hexString.padStart(32, '0')
}

fun String.isCorrectPhone() : Boolean {
    val pattern = Pattern.compile("\\+\\d{11,}")
    val matcher = pattern.matcher(this)
    return matcher.matches()
}