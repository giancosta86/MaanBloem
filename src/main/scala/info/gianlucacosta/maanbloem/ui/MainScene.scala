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

package info.gianlucacosta.maanbloem.ui

import java.io.IOException
import java.nio.file._
import java.util
import javafx.collections.FXCollections

import info.gianlucacosta.helios.apps.AppInfo
import info.gianlucacosta.helios.desktop.DesktopUtils
import info.gianlucacosta.helios.files.FileUtils
import info.gianlucacosta.helios.fx.about.AboutBox
import info.gianlucacosta.helios.fx.dialogs.{Alerts, InputDialogs}
import info.gianlucacosta.maanbloem.App
import info.gianlucacosta.maanbloem.moondeploy.MoonSettings
import info.gianlucacosta.maanbloem.moondeploy.descriptors.Descriptor

import scala.collection.JavaConversions._
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, Priority, VBox}

class MainScene(appInfo: AppInfo, descriptors: List[Descriptor]) extends Scene {
  private val aboutBox = new AboutBox(appInfo)

  stylesheets.add(
    App.getResource("App.css").toExternalForm
  )

  val galleryBox = new HBox {
    spacing = 10

    children = Seq(
      new Label("App gallery:") {
        id = "appGalleryPrompt"
      },

      new Label(MoonSettings.appGalleryDirectory.toString)
    )
  }

  val fxDescriptors = FXCollections.observableList(
    new util.ArrayList[Descriptor](descriptors)
  )

  val descriptorTable = new DescriptorTableView(fxDescriptors)

  val descriptorBox = new HBox {
    spacing = 15
    vgrow = Priority.Always

    children = Seq(
      descriptorTable,

      new VBox {
        padding = Insets(15)
        spacing = 35
        alignment = Pos.Center

        children = Seq(
          new Button {
            id = "runButton"
            styleClass += "appButton"

            text = "Run app"

            disable <== descriptorTable.selectionModel.value.selectedIndexProperty() === -1

            handleEvent(ActionEvent.Action) {
              (actionEvent: ActionEvent) => {
                actionEvent.consume()

                val descriptor = descriptorTable.selectionModel.value.getSelectedItem
                startApp(descriptor)
              }
            }
          },

          new Button {
            id = "detailsButton"
            styleClass += "appButton"

            text = "Details..."

            disable <== descriptorTable.selectionModel.value.selectedIndexProperty() === -1

            handleEvent(ActionEvent.Action) {
              (actionEvent: ActionEvent) => {
                actionEvent.consume()

                val descriptor = descriptorTable.selectionModel.value.getSelectedItem
                showDetails(descriptor)
              }
            }
          },

          new Button {
            id = "uninstallButton"
            styleClass += "appButton"

            text = "Uninstall..."

            disable <== descriptorTable.selectionModel.value.selectedIndexProperty() === -1

            handleEvent(ActionEvent.Action) {
              (actionEvent: ActionEvent) => {
                actionEvent.consume()

                val descriptor = descriptorTable.selectionModel.value.getSelectedItem

                val confirmationResult = InputDialogs.askYesNoCancel(
                  "Do you really wish to uninstall this app?",
                  s"${descriptor.name} ${descriptor.appVersion}")


                if (confirmationResult.contains(true)) {
                  try {
                    uninstall(descriptor)

                    fxDescriptors.remove(descriptor)

                    descriptorTable.refresh()

                    Alerts.showInfo(
                      "App successfully uninstalled.",
                      s"${descriptor.name} ${descriptor.appVersion}"
                    )
                  } catch {
                    case ex: IOException =>
                      Alerts.showException(
                        ex,
                        s"${descriptor.name} ${descriptor.appVersion}"
                      )
                  }
                }

                ()
              }
            }
          }
        )
      }
    )
  }


  val genericButtonsBox = new HBox {
    spacing = 15
    alignment = Pos.Center

    children = Seq(
      new Button {
        id = "aboutButton"

        text = s"About ${appInfo.name}..."

        handleEvent(ActionEvent.Action) {
          (actionEvent: ActionEvent) => {
            actionEvent.consume()

            aboutBox.show()
          }
        }
      }
    )
  }

  root = new VBox {
    spacing = 15
    padding = Insets(20)

    children = Seq(
      galleryBox,
      descriptorBox,
      genericButtonsBox
    )
  }


  private def startApp(descriptor: Descriptor): Unit = {
    DesktopUtils.openFile(
      descriptor.descriptorPath.toFile,

      ex => {
        ex.printStackTrace(System.err)

        Platform.runLater {
          Alerts.showWarning("Cannot run the selected app.\n\nIs MoonDeploy associated with files having .moondeploy extension?")
        }
      }
    )
  }


  private def showDetails(descriptor: Descriptor): Unit = {
    Alerts.showInfo(
      s"Name: ${descriptor.name}\nVersion: ${descriptor.appVersion}\n\nDescription: ${descriptor.description}\n\nPublisher: ${descriptor.publisher}\n\nDescriptor path: ${descriptor.descriptorPath}\n\nApp directory: ${descriptor.appDirectory}",
      s"${descriptor.name} ${descriptor.appVersion}"
    )
  }


  private def uninstall(descriptor: Descriptor): Unit = {
    if (!FileUtils.deltree(descriptor.appDirectory)) {
      throw new IOException("Could not uninstall the app")
    }

    var parentDirectory = descriptor.appDirectory.getParent

    while (true) {
      if (Files.list(parentDirectory).findFirst().isPresent) {
        return
      }

      Files.delete(parentDirectory)

      parentDirectory = parentDirectory.getParent
    }
  }
}
