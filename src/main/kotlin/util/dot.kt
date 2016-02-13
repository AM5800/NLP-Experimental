package util

fun dot(x: Iterable<Double>, y: Iterable<Boolean>): Double {
  return x.zip(y).map { pair ->
    if (pair.second) pair.first else 0.0
  }.sum()
}

fun dot(x: DoubleArray, y: BooleanArray): Double {
  if (x.size != y.size) throw Exception("x and y size mismatch")
  return x.mapIndexed { i, d -> if (y[i]) d else 0.0 }.sum()
}


