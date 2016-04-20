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

import scalafx.scene.control.{Alert, ButtonBar, ButtonType}


/**
  * Shows common input dialogs
  */
case object InputDialogs {
  def askYesNoCancel(message: String, header: String = ""): Option[Boolean] = {
    val yesButton = new ButtonType("Yes")
    val noButton = new ButtonType("No")
    val cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CancelClose)

    val alert = new Alert(Alert.AlertType.Confirmation) {
      headerText = header

      contentText = message

      buttonTypes = List(
        yesButton,
        noButton,
        cancelButton
      )
    }

    val inputResult = alert.showAndWait()

    inputResult match {
      case Some(`yesButton`) =>
        Some(true)

      case Some(`noButton`) =>
        Some(false)

      case _ =>
        None
    }
  }
}