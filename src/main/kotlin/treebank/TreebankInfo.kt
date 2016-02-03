package treebank

import java.io.File

class TreebankInfo(val infoFile: File,
                   val formatId: String,
                   val treebankPath: File,
                   val metadata: Map<String, String>) {

}