#!/usr/bin/env scala

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Paths, StandardCopyOption}
import scala.io.StdIn.readLine

object PrepareImages {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println("Usage: PrepareImages <input_directory> <output_directory>")
      sys.exit(1)
    }

    val inputDir = new File(args(0))
    val outputDir = new File(args(1))

    if (!inputDir.exists() || !inputDir.isDirectory) {
      println(s"Input directory ${inputDir.getAbsolutePath} does not exist or is not a directory.")
      sys.exit(1)
    }

    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }

    def processFileName(fileName: String): String = {
      fileName
        .replaceAll("[áàäâã]", "a")
        .replaceAll("[éèëê]", "e")
        .replaceAll("[íìïî]", "i")
        .replaceAll("[óòöôõ]", "o")
        .replaceAll("[úùüû]", "u")
        .replaceAll("[ñ]", "n")
        .replaceAll("[ç]", "c")
        .replaceAll("-\\s", "")
        .replaceAll("\\s", "_")
        .toLowerCase()
    }

    def copyFiles(srcDir: File, destDir: File): Unit = {
      srcDir.listFiles().filter(n => n.getName() != ".DS_Store").foreach { file =>
        val destFile = new File(destDir, processFileName(file.getName))
        if (file.isDirectory) {
          destFile.mkdirs()
          copyFiles(file, destFile)
        } else {
          Files.copy(file.toPath, destFile.toPath, StandardCopyOption.REPLACE_EXISTING)
        }
      }
    }

    copyFiles(inputDir, outputDir)
    println(s"Files copied from ${inputDir.getAbsolutePath} to ${outputDir.getAbsolutePath}")
  }
}

PrepareImages.main(args)