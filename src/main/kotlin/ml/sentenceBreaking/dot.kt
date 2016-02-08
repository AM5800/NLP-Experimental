package ml.sentenceBreaking

fun dot(x: DoubleArray, y: BooleanArray): Double {
  if (x.size != y.size) throw Exception("x and y size mismatch")

  return x.mapIndexed { i, d -> if (y[i]) d else 0.0 }.sum()
}


