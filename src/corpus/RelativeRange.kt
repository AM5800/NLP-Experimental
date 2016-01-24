package corpus

public class RelativeRange(public val start: Double, public val end: Double) {
    init {
        if (start < 0) throw IllegalArgumentException("start can't be less than zero")
        if (end > 1) throw IllegalArgumentException("end can't be more than one")
        if (start > end) throw IllegalArgumentException("start can't be more than end")
    }

    fun accept(i: Int, n: Int): Boolean {
        val value = i.toDouble() / n
        return value > start && value <= end
    }
}