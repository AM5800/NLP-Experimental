package util

import java.io.File

public fun File.changeExtension(newExtension: String): File = File(this.parentFile, this.nameWithoutExtension + ".shuffled")
