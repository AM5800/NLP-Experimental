package ml.sentenceBreaking

class WordBounds(val start: Int, val end: Int)

fun splitWords(text: String): List<WordBounds> {
    val words = mutableListOf<WordBounds>()

    var wordStart = -1
    var letterMode = false
    var prevChar: Char? = null
    (text + " ").forEachIndexed { i, c ->
        val isWs = c.isWhitespace()
        val isLetter = c.isLetterOrDigit()

        if (wordStart == -1 && !isWs) {
            wordStart = i
            letterMode = isLetter
        } else if (wordStart != -1 && isWs) {
            words.add(WordBounds(wordStart, i))
            wordStart = -1
        } else if (wordStart != -1 && (isLetter == !letterMode || !letterMode && c != prevChar)) {
            words.add(WordBounds(wordStart, i))
            letterMode = isLetter
            wordStart = i
        }
        prevChar = c
    }
    return words
}