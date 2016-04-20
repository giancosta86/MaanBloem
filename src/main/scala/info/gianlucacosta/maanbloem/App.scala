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

package info.gianlucacosta.maanbloem


import javafx.application.Application
import javafx.stage.Stage

import info.gianlucacosta.maanbloem.ui.{LoadingScene, LoadingThread}


object App {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[App], args: _*)
  }
}

class App extends Application {
  override def start(primaryStage: Stage): Unit = {
    primaryStage.setWidth(1200)
    primaryStage.setHeight(700)

    primaryStage.setTitle(s"${AppInfo.name} - ${AppInfo.description}")
    primaryStage.getIcons.add(AppInfo.mainIcon)

    val loadingScene = new LoadingScene
    primaryStage.setScene(loadingScene)

    primaryStage.show()

    val loadingThread = new LoadingThread(primaryStage)
    loadingThread.start()
  }
}
