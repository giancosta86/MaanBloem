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

package info.gianlucacosta.maanbloem.moondeploy.descriptors

import java.io.{File, FileFilter, IOException}
import java.nio.file.{Files, Path}

import scala.collection.mutable


object DescriptorFinder {
  def findDescriptors(galleryDirectory: Path): List[Path] = {
    if (!Files.isDirectory(galleryDirectory)) {
      return List()
    }


    val descriptors = mutable.MutableList[Path]()

    visitDirectory(galleryDirectory.toFile, descriptors)

    descriptors.toList
  }


  private def visitDirectory(directory: File, cumulatedDescriptors: mutable.MutableList[Path]): Unit = {
    val regularFiles = directory.listFiles(new FileFilter {
      override def accept(file: File): Boolean =
        file.isFile
    })

    if (regularFiles == null) {
      throw new IOException(s"Could not list the files in ${directory}")
    }

    val appDescriptor =
      regularFiles
        .find(file => file.getName.endsWith(".moondeploy"))


    if (appDescriptor.isDefined) {
      cumulatedDescriptors += appDescriptor.get.toPath
      return
    }


    val subDirectories = directory.listFiles(new FileFilter {
      override def accept(file: File): Boolean =
        file.isDirectory
    })

    if (subDirectories == null) {
      throw new IOException(s"Could not list the directories in ${directory}")
    }

    subDirectories.foreach(subDirectory => {
      visitDirectory(subDirectory, cumulatedDescriptors)
    })
  }
}
