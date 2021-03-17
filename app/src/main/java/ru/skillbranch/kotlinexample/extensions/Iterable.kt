package ru.skillbranch.kotlinexample.extensions


fun <T>List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
      val iterator = iterator()
      var included = false
      val resultList = mutableListOf<T>()

      while (iterator.hasNext()) {
          val elem = iterator.next()

          if (predicate(elem) || included) {
              included = true
          } else {
              resultList.add(elem)
          }

      }

     return resultList
}