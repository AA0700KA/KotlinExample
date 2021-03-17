package ru.skillbranch.kotlinexample


import ru.skillbranch.kotlinexample.extensions.fullNameToPair
import ru.skillbranch.kotlinexample.extensions.isCorrectPhone

class UserHolder {

     val usersMap = mutableMapOf<String, User>()

    fun registerUser(fullName : String, email : String, password : String) : User {
        usersMap[email]?.let {
            throw IllegalArgumentException("A user with this email already exists")
        }

        return User.makeUser(fullName, email, password).also {
            usersMap[it.login!!] = it
        }

    }

    fun registerUserByPhone(fullName : String, rawPhone : String) : User {

         usersMap[rawPhone]?.let {
             throw java.lang.IllegalArgumentException("A user with this phone already exists")
         }

        return User.makeUser(fullName, phone = rawPhone).also {
            usersMap[it.login!!] = it
        }

    }
    /*
         accesCode generate randomly, becouse if user login into phone number password make a null
     */

    fun loginUser(login : String, password: String? = null) : String? {

         usersMap[login]?.let {
             if (checkPassword(it, password)) {
                 return it.userInfo
             }
         }

        return null
    }

    private fun checkPassword(user : User, password: String?) : Boolean
            = if (user.login.isCorrectPhone()) user.checkAccessCode() else user.checkPassword(password!!)


    fun requestAccessCode(login: String) : Unit {
         usersMap[login]?.let {
             it.changeAccessCode()
         }
    }

    fun importUsers(list: List<String>) : List<User> {

        val resultList = mutableListOf<User>()

            for (value in list) {
                val stringData = value.split(";")
                val fullName = stringData[0]
                val email = stringData[1]
                val salt = stringData[2].split(":")[0]
                val passwordHash = stringData[2].split(":")[1]
                val phone = if (stringData[3].isEmpty()) null else stringData[3]
                val (firstName, lastName) = fullName.fullNameToPair()

                resultList.add(User(firstName, lastName, email, salt, passwordHash, phone).also {
                    usersMap[it.login] = it
                })

            }

        return resultList
    }

}