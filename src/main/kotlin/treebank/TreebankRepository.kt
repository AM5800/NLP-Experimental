package treebank

import java.io.*
import java.util.*

class TreebankRepository(basePath: File) {
  private val treebanks = parseInfos(basePath)

  private fun parseInfos(basePath: File): List<TreebankInfo> {
    val fileList = basePath.listFiles { f -> f.extension.equals("treebank", true) }

    return fileList.map { file -> tryParseTreebankInfo(file.absoluteFile) }.filterNotNull()
  }

  private fun tryParseTreebankInfo(infoFile: File): TreebankInfo? {
    try {
      return BufferedReader(FileReader(infoFile)).use { reader ->
        val id = reader.readLine()!!.trim()
        val path = reader.readLine()!!.trim()
        val metadata = LinkedHashMap<String, String>()

        while (true) {
          val line = reader.readLine() ?: break

          val splitted = line.split("=").map { it.trim() }.toList()
          if (splitted.size != 2) continue

          metadata.put(splitted[0], splitted[1])
        }

        val treebankPath = File(infoFile.parentFile, path)
        return TreebankInfo(infoFile, id, treebankPath, metadata)
      }

    } catch (e: Exception) {
      // TODO need logging!
      val sw = StringWriter()
      e.printStackTrace(PrintWriter(sw))
      println(sw.toString())
      return null
    }
  }

  fun getTreebanks(): List<TreebankInfo> {
    return treebanks
  }
}