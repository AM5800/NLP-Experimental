package util

fun Int.clamp(lower: Int, upper: Int = this): Int {
  if (this < lower) return lower
  if (this > upper) return upper
  return this
}

