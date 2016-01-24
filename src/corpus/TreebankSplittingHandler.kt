package corpus

import corpus.parsing.TreebankParserHandler
import util.changeExtension
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


public class TreebankSplittingHandler(private val origin: TreebankParserHandler,
                                      private val range: RelativeRange) : TreebankParserHandler() {
    private val acceptedIds = HashSet<String>()
    private var ignore = false

    override fun beginTreebank(path: String) {
        val shufflePath = File(path).changeExtension(".shuffled")

        BufferedReader(FileReader(shufflePath)).use { reader ->
            val n = Integer.parseInt(reader.readLine())
            repeat(n, { i ->
                val line = reader.readLine()?.trim()
                if (line != null && range.accept(i, n)) acceptedIds.add(line)
            })
        }
        origin.beginTreebank(path)
    }

    override fun startSentence(id: String) {
        ignore = !acceptedIds.contains(id)

        if (!ignore) origin.startSentence(id)
    }

    override fun word(word: String, lemma: String, pos: String?) {
        if (!ignore) origin.word(word, lemma, pos)
    }

    override fun endSentence() {
        if (!ignore) origin.endSentence()
    }

    override fun endTreebank() {
        origin.endTreebank()
    }
}