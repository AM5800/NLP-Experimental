package corpus

import corpus.parsing.CorpusParserHandler
import util.changeExtension
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

public class CorpusRange(public val start: Double, public val end: Double) {
    init {
        if (start < 0) throw IllegalArgumentException("start can't be less than zero")
        if (end > 1) throw IllegalArgumentException("end can't be more than one")
        if (start > end) throw IllegalArgumentException("start can't be more than end")
    }

    fun accept(i: Int, n: Int): Boolean {
        val value = i.toDouble() / n
        return value > start && value <= end
    }
}


public class CorpusSplittingHandler(private val origin: CorpusParserHandler,
                                    private val range: CorpusRange) : CorpusParserHandler() {
    private val acceptedIds = HashSet<String>()
    private var ignore = false

    override fun beginCorpus(path: String) {
        val shufflePath = File(path).changeExtension(".shuffled")

        BufferedReader(FileReader(shufflePath)).use { reader ->
            val n = Integer.parseInt(reader.readLine())
            repeat(n, { i ->
                val line = reader.readLine()?.trim()
                if (line != null && range.accept(i, n)) acceptedIds.add(line)
            })
        }
        origin.beginCorpus(path)
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

    override fun endCorpus() {
        origin.endCorpus()
    }
}