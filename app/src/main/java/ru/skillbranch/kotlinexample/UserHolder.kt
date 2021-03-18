package ru.skillbranch.kotlinexample

import ru.skillbranch.kotlinexample.User.Factory.fullNameToPair
import ru.skillbranch.kotlinexample.User.Factory.isCorrectPhone
import ru.skillbranch.kotlinexample.User.Factory.nullOrCurrent
import ru.skillbranch.kotlinexample.User.Factory.toLogin


object UserHolder {

     val usersMap = mutableMapOf<String, User>()

    fun registerUser(fullName : String, email : String, password : String) : User {
        usersMap[email.toLowerCase()]?.let {
            throw IllegalArgumentException("A user with this email already exists")
        }

        return User.makeUser(fullName, email, password).also {
            usersMap[it.login!!] = it
        }

    }

    fun registerUserByPhone(fullName : String, rawPhone : String) : User {

         usersMap[rawPhone.replace(Regex("[\\s\\-\\(\\)]"), "")]?.let {
             throw java.lang.IllegalArgumentException("A user with this phone already exists")
         }

        return User.makeUser(fullName, phone = rawPhone).also {
            usersMap[it.login!!] = it
        }

    }
    /*
         accessCode generate randomly, becouse if user login into phone number password make a null
     */

    fun loginUser(login : String, password: String) : String? {

         usersMap[login.toLogin()]?.let {
             if (it.checkPassword(password)) {
                 return it.userInfo
             }
         }

        return null
    }


    fun requestAccessCode(login: String) : Unit {
         usersMap[login.toLogin()]?.let {
             it.changeAccessCode()
         }
    }

    fun importUsers(list: List<String>) : List<User> {

        val resultList = mutableListOf<User>()

            for (value in list) {
                val stringData = value.split(";")
                val fullName = stringData[0].trim()
                val email = stringData[1].nullOrCurrent()?.trim()
                val salt = stringData[2].split(":")[0].trim()
                val passwordHash = stringData[2].split(":")[1].trim()
                val phone = stringData[3].nullOrCurrent()?.trim()
                val (firstName, lastName) = fullName.fullNameToPair()

                resultList.add(User(firstName, lastName, email, salt, passwordHash, phone).also {
                    usersMap[it.login] = it
                })

            }

        return resultList
    }

}