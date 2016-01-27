package corpus

import corpus.parsing.TreebankParserHandler
import util.shuffle
import java.io.File
import java.io.PrintWriter
import java.util.*

public class ShufflingHandler : TreebankParserHandler() {
    private val ids = ArrayList<String>()

    override fun beginSentence(id: String, sentenceNumber: Int?, totalSentences: Int?) {
        ids.add(id)
    }

    fun save(path: File) {
        val shuffled = ids.shuffle()

        PrintWriter(path, "UTF-8").use { writer ->
            writer.println(shuffled.size)
            shuffled.forEach { writer.println(it) }
        }
    }
}