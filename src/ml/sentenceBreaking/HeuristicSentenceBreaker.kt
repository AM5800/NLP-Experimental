package ml.sentenceBreaking

public data class Sentence(public val start : Int, public val end : Int)

public class HeuristicSentenceBreaker {
    fun setNonBreakingWords(nonBreakers: Iterable<String>) {
    }

    public fun breakText(text : String) : Iterable<Sentence> {
        var startIndex = 0

        val result = arrayListOf<Sentence>()

        while (true) {
            val index = text.indexOf('.', startIndex)
            if (index == -1) break

            result.add(Sentence(startIndex, index))

            startIndex = index + 1
        }

        return result
    }
}