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

import java.io.{File, FileReader}
import java.net.URL
import java.nio.file.Path
import javax.json.{Json, JsonObject, JsonString}

import info.gianlucacosta.helios.desktop.Os


object DescriptorParser {
  private val EmptyObject = Json.createObjectBuilder().build()

  def parse(descriptorPath: Path): Option[Descriptor] = {
    val jsonReader = Json.createReader(new FileReader(descriptorPath.toFile))

    try {
      val descriptorJson = jsonReader.readObject()

      val name = descriptorJson.getString("Name")
      val appVersion = descriptorJson.getString("Version")
      val publisher = descriptorJson.getString("Publisher")
      val description = descriptorJson.getString("Description")
      val baseUrl = descriptorJson.getString("BaseURL")

      val currentGoOs = getCurrentGoOs

      val iconPath = parseIconPath(descriptorJson, currentGoOs)

      val iconFile =
        if (iconPath != "") {
          val appFilesDir = descriptorPath.getParent.resolve("files").toFile
          Some(new File(appFilesDir, iconPath))
        } else {
          None
        }

      Some(Descriptor(
        descriptorPath = descriptorPath,
        name = name,
        appVersion = appVersion,
        publisher = publisher,
        description = description,
        baseUrl = new URL(baseUrl),
        iconFile = iconFile)
      )
    } catch {
      case ex: Exception =>
        System.err.println(
          s"Error while parsing '${descriptorPath}' --> ${ex.getClass.getSimpleName}: ${ex.getMessage}"
        )
        System.err.println()
        ex.printStackTrace(System.err)
        System.err.println()
        System.err.println()

        None

    } finally {
      jsonReader.close()
    }
  }


  private def getCurrentGoOs: String =
    if (Os.isLinux) {
      "linux"
    } else if (Os.isWindows) {
      "windows"
    } else if (Os.isMac) {
      "darwin"
    } else {
      throw new RuntimeException("Unsupported OS")
    }


  private def parseIconPath(descriptorJson: JsonObject, currentGoOs: String): String = {
    var iconPath = ""

    val osJson = descriptorJson.getJsonObject("OS")

    if (osJson != null) {
      val currentGoOsJson = osJson.getJsonObject(currentGoOs)

      if (currentGoOsJson != null) {
        iconPath = currentGoOsJson.getString("IconPath", "")
      }
    }

    if (iconPath != "") {
      return iconPath
    }

    val iconPathValue = descriptorJson.get("IconPath")

    iconPathValue match {
      case iconPathJson: JsonObject =>

        iconPath = iconPathJson.getString(currentGoOs, "")
        if (iconPath != "") {
          return iconPath
        }

        iconPath = iconPathJson.getString("*", "")
        if (iconPath != "") {
          return iconPath
        }

      case iconPathString: JsonString =>
        return iconPathString.getString
    }


    iconPath
  }
}
