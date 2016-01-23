package corpus

import corpus.parsing.CorpusParserHandler
import util.changeExtension
import util.shuffle
import java.io.File
import java.io.PrintWriter
import java.util.*

public class ExternalShuffler : CorpusParserHandler() {
    private var path: File? = null
    private val ids = ArrayList<String>()

    override fun beginCorpus(path: String) {
        this.path = File(path)
    }

    override fun startSentence(id: String) {
        ids.add(id)
    }

    override fun endCorpus() {
        val shuffled = ids.shuffle()

        val path = this.path!!

        val newPath = path.changeExtension(".shuffled")

        PrintWriter(newPath, "UTF-8").use { writer ->
            writer.println(shuffled.size)
            shuffled.forEach { writer.println(it) }
        }

        this.path = null
    }
}