/*§
  ===========================================================================
  MaanBloem
  ===========================================================================
  Copyright (C) 2016 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.maanbloem.utils

import java.awt.Desktop
import java.io.{File, IOException}
import java.net.URI
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

/**
  * Desktop utilities
  */
object DesktopUtils {
  private class DeltreeVisitor extends SimpleFileVisitor[Path]() {
    var _errorsFound = false

    def errorsFound = _errorsFound

    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
      try {
        Files.delete(file)
        FileVisitResult.CONTINUE
      } catch {
        case _: Exception =>
          _errorsFound = true
          FileVisitResult.TERMINATE
      }
    }

    override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
      try {
        Files.delete(dir)
        FileVisitResult.CONTINUE
      } catch {
        case _: Exception =>
          _errorsFound = true
          FileVisitResult.TERMINATE
      }
    }
  }

  
  private def runInThread(action: (Desktop) => Unit) {
    val externalThread = new Thread() {
      override def run() {
        val desktop = Desktop.getDesktop

        if (desktop == null) {
          throw new UnsupportedOperationException()
        }

        action(desktop)
      }
    }
    externalThread.start()
  }


  /**
    * Opens the given URL in a browser, without freezing the app
    *
    * @param url
    */
  def openBrowser(url: String) {
    runInThread(desktop => desktop.browse(new URI(url)))
  }


  /**
    * Opens the given file using the user's desktop environment settings, without freezing the app
    *
    * @param file
    */
  def openFile(file: File): Unit = {
    runInThread(desktop => desktop.open(file))
  }

  /**
    * Returns the user's home directory, if available
    *
    * @return Some(user home directory) or None
    */
  def homeDirectory: Option[File] = {
    val userHomeProperty = System.getProperty("user.home")

    if (userHomeProperty == null) {
      None
    } else {
      Some(new File(userHomeProperty))
    }
  }


  /**
    * Deletes the given directory and its subtree
    *
    * @param directory The directory to delete
    * @return Returns true if the directory did not exist or was successfully deleted
    */
  def deltree(directory: Path): Boolean = {
    if (!Files.exists(directory)) {
      return true
    }

    require(Files.isDirectory(directory))

    val visitor = new DeltreeVisitor

    Files.walkFileTree(directory, visitor)

    !visitor.errorsFound
  }

  private lazy val os = System.getProperty("os.name").toLowerCase()

  lazy val isWindows = os.contains("windows")

  lazy val isLinux = os.contains("linux")

  lazy val isMac = os.contains("mac")
}
