import corpus.ExternalShuffler
import corpus.parsing.CorpusParserHandler
import corpus.parsing.NegraParser
import java.nio.file.Paths
import java.util.*

fun main(args : Array<String>) {
    System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());

    val handler = SentenceBreakingHandler()
    val negraParser = NegraParser()
    negraParser.parse("data\\corpuses\\tiger_release_aug07.corrected.16012013.xml", handler)
    negraParser.parse("data\\corpuses\\tiger_release_aug07.corrected.16012013.xml", ExternalShuffler())

    for (nonBreaker in handler.nonBreakers) {
        println("Non breaker: " + nonBreaker)
    }
}

class SentenceBreakingHandler : CorpusParserHandler() {
    private var previousWord : String? = null
    private var currentNonBreaker : String? = null
    public val nonBreakers = HashSet<String>()

    override fun startSentence(id: String) {
        previousWord = null
        currentNonBreaker = null
    }

    override fun word(word: String, lemma: String, pos: String?) {

        if (word.contains(".")) nonBreakers.add(word)
        if (lemma.contains(".")) nonBreakers.add(word)

        val prev = previousWord
        val breaker = currentNonBreaker

        if (pos == "--" || pos?.startsWith("$") == true) {
            currentNonBreaker = null
        }
        else if (breaker != null) {
            nonBreakers.add(breaker)
        }

        if (prev != null && word == ".") {
            currentNonBreaker = prev
        }

        previousWord = word
    }

    override fun endSentence() {

    }

}

class OutputHandler : CorpusParserHandler() {
    override fun startSentence(id: String) {
        println("Started sentence: " + id)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        println("\tWord " + word)
    }

    override fun endSentence() {

    }

}
