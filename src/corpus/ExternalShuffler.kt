package corpus

import corpus.parsing.CorpusParserHandler
import java.io.File
import java.io.PrintWriter
import java.util.*

public fun <T> List<T>.shuffle(): List<T> {
    val random = Random()
    val list = this.toArrayList()
    for (i in (list.count() - 1) downTo 1) {
        val j = random.nextInt(i + 1)
        val tmp = list[i]
        list[i] = list[j]
        list[j] = tmp
    }
    return list
}

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

        val newPath = File(path.parentFile, path.nameWithoutExtension + ".shuffled")

        PrintWriter(newPath, "UTF-8").use { writer ->
            writer.println(shuffled.size)
            shuffled.forEach { writer.println(it) }
        }

        this.path = null
    }
}