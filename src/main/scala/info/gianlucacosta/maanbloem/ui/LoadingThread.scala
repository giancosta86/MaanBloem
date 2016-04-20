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

package info.gianlucacosta.maanbloem.ui

import javafx.stage.Stage

import info.gianlucacosta.maanbloem.moondeploy.MoonSettings
import info.gianlucacosta.maanbloem.moondeploy.descriptors.{DescriptorFinder, DescriptorParser}

import scalafx.application.Platform

class LoadingThread(stage: Stage) extends Thread {
  setDaemon(true)

  override def run(): Unit = {
    try {
      val appGalleryPath = MoonSettings.appGalleryDirectory

      val descriptors =
        DescriptorFinder
          .findDescriptors(appGalleryPath)
          .map(DescriptorParser.parse)
          .filter(_.isDefined)
          .map(_.get)

      Platform.runLater {
        val mainScene = new MainScene(descriptors)
        stage.setScene(mainScene)
      }
    } catch {
      case ex: Exception =>
        Platform.runLater {
          Alerts.showException(ex, "Initialization error")

          ex.printStackTrace(System.err)
          System.exit(1)
        }
    }
  }
}
