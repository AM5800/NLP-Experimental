package treebank.parsing.shuffling

import treebank.TreebankInfo
import treebank.TreebankRepository
import treebank.parsing.SentenceSelector
import treebank.parsing.TreebankParserHandler
import treebank.parsing.TreebankParsersSet
import util.shuffle
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.*
import java.util.logging.Logger


class TreebankShuffleRepository(private val treebanksRepo: TreebankRepository,
                                private val parsers: TreebankParsersSet,
                                private val logger: Logger) {

  fun shuffleIfNeeded(selector: SentenceSelector) {
    for (treebankInfo in treebanksRepo.getTreebanks()) {
      val shuffleFilePath = getShufflePath(treebankInfo) // TODO better error handling
      if (shuffleFilePath.exists()) continue

      logger.info("Shuffling $shuffleFilePath")

      selector.clear()
      parsers.parse(treebankInfo, selector)
      val ids = selector.getIds()

      val shuffled = ids.shuffle()

      PrintWriter(shuffleFilePath, "UTF-8").use { writer ->
        writer.println(shuffled.size)
        shuffled.forEach { writer.println(it) }
      }

      logger.info("Shuffle done")
    }
  }

  private fun getShufflePath(treebankInfo: TreebankInfo): File {
    return File(treebankInfo.infoFile.parentFile, treebankInfo.metadata["shuffleFile"]!!)
  }

  fun newHandler(handler: TreebankParserHandler, range: RelativeRange): TreebankParserHandler {
    return ReadTreebankShuffleHandler(this, handler, range)
  }

  fun getShuffle(info: TreebankInfo): HashMap<String, Int>? {
    val shuffleFile = getShufflePath(info)
    return loadShuffleInfo(shuffleFile)
  }

  private fun loadShuffleInfo(shuffleFile: File): HashMap<String, Int> {
    val result = LinkedHashMap<String, Int>()
    BufferedReader(FileReader(shuffleFile)).use { reader ->
      val n = Integer.parseInt(reader.readLine())
      repeat(n, { i ->
        val line = reader.readLine()?.trim()
        if (line != null) result.put(line, i)
      })
    }
    return result
  }
}