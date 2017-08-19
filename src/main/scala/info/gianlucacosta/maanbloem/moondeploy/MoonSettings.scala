/*§
  ===========================================================================
  MaanBloem
  ===========================================================================
  Copyright (C) 2016-2017 Gianluca Costa
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

package info.gianlucacosta.maanbloem.moondeploy

import java.io.{File, FileReader}
import java.nio.file.Paths
import javax.json.Json

import info.gianlucacosta.helios.desktop.DesktopUtils

object MoonSettings {
  lazy val localDirectory = {
    val userDirectory = DesktopUtils.homeDirectory

    if (userDirectory.isEmpty) {
      throw new RuntimeException("Cannot find the user directory")
    }

    val defaultLocalDirectory = new File(userDirectory.get, "MoonDeploy")

    val settingsFile = new File(userDirectory.get, ".moondeploy.json")

    if (settingsFile.isFile) {
      val settingsReader = Json.createReader(new FileReader(settingsFile))

      try {
        val settings = settingsReader.readObject()

        Paths.get(
          settings.getString("LocalDirectory", defaultLocalDirectory.getAbsolutePath)
        )
      } catch {
        case ex: Exception =>
          defaultLocalDirectory.toPath
      } finally {
        settingsReader.close()
      }

    } else {
      defaultLocalDirectory.toPath
    }
  }

  lazy val appGalleryDirectory = localDirectory.resolve("apps")
}
