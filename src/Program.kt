import corpusParsers.CorpusParserHandler
import corpusParsers.NegraParser
import java.nio.file.Paths

fun main(args : Array<String>) {
    System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());

    NegraParser().parse("data\\corpuses\\tiger_release_aug07.corrected.16012013.xml", OutputHandler())
}

class OutputHandler : CorpusParserHandler{
    override fun startSentence(id: String) {
        println("Started sentence: " + id)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        println("\tWord " + word)
    }

    override fun endSentence() {

    }

}
