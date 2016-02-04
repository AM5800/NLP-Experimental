package ml.sentenceBreaking

import java.util.*
import java.util.regex.Pattern


class SentenceBreakerUtils {
  companion object {
    val romanNumberRegex = Pattern.compile("[IVXLCDM]+")
    val numberRegex = Pattern.compile("[0-9\\.,]+")
    val punctuationChars = LinkedHashSet<Char>(".!?,<>()[]{}!:;'`".toList())
    val abbreviationRegex = Pattern.compile("(\\w\\.)+")

    fun isSentenceEndChar(ch: Char): Boolean {
      return ch == '.' || ch == '?' || ch == '!'
    }

    fun isNumber(word: String): Boolean {
      if (numberRegex.matcher(word).matches()) return true
      if (romanNumberRegex.matcher(word).matches()) return true
      return false
    }

    fun isWebLink(word: String): Boolean {
      val lower = word.toLowerCase()
      if (lower.startsWith("www")) return true
      if (lower.startsWith("http")) return true
      if (lower.endsWith(".de")) return true

      return false
    }

    fun isPunctuation(c: Char): Boolean {
      return punctuationChars.contains(c)
    }

    fun isAbbreviation(word: String): Boolean {
      return abbreviationRegex.matcher(word).matches()
    }
  }
}