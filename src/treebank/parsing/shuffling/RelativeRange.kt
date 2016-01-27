package treebank.parsing.shuffling

public class RelativeRange(public val start: Int, public val end: Int) {
    init {
        if (start < 0) throw IllegalArgumentException("start can't be less than zero")
        if (end > 100) throw IllegalArgumentException("end can't be more than hundred")
        if (start > end) throw IllegalArgumentException("start can't be more than end")
    }

    fun accept(i: Int, n: Int): Boolean {
        val value = i / n
        return value >= start && value <= end
    }
}