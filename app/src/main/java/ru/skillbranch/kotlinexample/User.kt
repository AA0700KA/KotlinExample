package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import ru.skillbranch.kotlinexample.user_extensions.fullNameToPair

import ru.skillbranch.kotlinexample.user_extensions.isCorrectPhone
import ru.skillbranch.kotlinexample.user_extensions.md5
import java.lang.IllegalArgumentException
import java.security.SecureRandom
import java.util.*

class User private constructor(val firstName: String,
                               val lastName: String?,
                               email: String? = null,
                               rawPhone: String? = null,
                               var meta: Map<String, Any>?) {


    val userInfo : String

    val fullName: String
        get() = "${firstName} ${lastName}"

    val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString (" ")

    var phone: String? = null
        get() {
            return field
        }
        set(value) {
            value?.let {
                if (it.isCorrectPhone()) {
                    field = it
                } else {
                    throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
                }
            }
        }

    var login: String
    private var salt: String? = null

    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    init {
        phone = rawPhone
        login = if (!phone.isNullOrBlank()) phone!! else email!!

        userInfo = """
           firstName: $firstName
           lastName: $lastName
           login: $login
           fullName: $fullName
           initials: $initials
           email: $email
           phone: $phone
           meta: $meta
           """
    }

    constructor(
        firstName: String,
        lastName: String?,
        email: String?,
        password: String?
    ) : this(firstName, lastName, email = email, meta = mapOf("auth" to "password")) {
        passwordHash = encrypt(password)
    }

    constructor(firstName: String,
                lastName: String?,
                rawPhone: String?) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")) {
          generateAccessCode()
          passwordHash = encrypt(accessCode)
    }

    constructor(firstName: String,
                lastName: String?,
                email : String?,
                salt : String?,
                passwordHash : String,
                rawPhone: String?) : this(firstName, lastName, email, rawPhone, mapOf("src" to "csv")) {
        this.salt = salt
        this.passwordHash = passwordHash

        if (!rawPhone.isNullOrEmpty()) {
            generateAccessCode()
            this.passwordHash = encrypt(accessCode)
            this.meta = mapOf("auth" to "sms")
        }

    }

    fun checkPassword(password: String) : Boolean {
        val encryptData = if (accessCode.isNullOrEmpty()) encrypt(password) else encrypt(password).plus(encrypt(accessCode))
        return encryptData.equals(passwordHash)
    }

    fun changeAccessCode() {
        generateAccessCode()

        meta?.let {map ->
            map["auth"]?.let {
                passwordHash = if (it.equals("sms")) encrypt(accessCode) else passwordHash.plus(encrypt(accessCode))
                return
            }

            passwordHash = passwordHash.plus(encrypt(accessCode))
        }


    }

    private fun generateAccessCode() {
        accessCode = ""
        val symbols = "ABCDEFGHIJKLMNOPQRSTUVWXWZabcdefghijklmnopqrstuvwxwz0123456789"
        val random = Random()

        repeat(6) {
            accessCode = accessCode.plus(symbols[random.nextInt(62)])
        }
    }

    private fun encrypt(password: String?) : String {

        if (salt.isNullOrEmpty()) {
            salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()
        }

        println("while encrypt ${salt}")
        return salt.plus(password).md5()
    }

    fun checkAccessCode() : Boolean {
        println("Write access code ${accessCode}")
        return passwordHash.equals(encrypt(accessCode))
    }

    companion object {
        fun makeUser(fullName: String,
                     email: String? = null,
                     password: String? = null,
                     phone: String? = null) : User {

            var (firstName, lastName) = fullName.fullNameToPair()

            return when {
                !email.isNullOrBlank() -> User(firstName, lastName, email, password)
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                else -> throw IllegalAccessException("Email or phone must be not null or blank")
            }

        }

    }


}