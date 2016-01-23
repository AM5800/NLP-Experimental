package util

import java.util.*

public fun <T> List<T>.shuffle(): List<T> {
    val random = Random()
    val list = this.toArrayList()
    for (i in (list.count() - 1) downTo 1) {
        val j = random.nextInt(i + 1)
        val tmp = list[i]
        list[i] = list[j]
        list[j] = tmp
    }
    return list
}