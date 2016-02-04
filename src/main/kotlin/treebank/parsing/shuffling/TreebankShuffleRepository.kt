package treebank.parsing.shuffling

import treebank.TreebankInfo
import treebank.TreebankRepository
import treebank.parsing.TreebankParserHandler
import treebank.parsing.TreebankParsersSet
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


class TreebankShuffleRepository(private val treebanksRepo: TreebankRepository,
                                private val parsers: TreebankParsersSet) {
  fun shuffleIfNeeded() {
    for (treebankInfo in treebanksRepo.getTreebanks()) {
      val shuffleFilePath = getShufflePath(treebankInfo) // TODO better error handling
      if (shuffleFilePath.exists()) continue

      val handler = CreateTreebankShuffleHandler()
      parsers.parse(treebankInfo, handler)
      handler.save(shuffleFilePath)
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