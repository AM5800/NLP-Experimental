package ml.sentenceBreaking

import java.util.regex.Pattern


class SentenceBreakerUtils {
    companion object {
        public val RomanNumberRegex = Pattern.compile("[IVXLCDM]+")
        public val NumberRegex = Pattern.compile("[0-9\\.,]+")

        public fun isSentenceEndChar(ch: Char): Boolean {
            return ch == '.' || ch == '?' || ch == '!'
        }

        fun isNumber(word: String): Boolean {
            if (NumberRegex.matcher(word).matches()) return true
            if (RomanNumberRegex.matcher(word).matches()) return true
            return false
        }
    }
}