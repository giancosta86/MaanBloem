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

import java.io.File
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.ObservableList

import info.gianlucacosta.maanbloem.moondeploy.descriptors.Descriptor

import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{Label, TableCell, TableColumn, TableView}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority


class DescriptorTableView(fxDescriptors: ObservableList[Descriptor]) extends TableView[Descriptor](fxDescriptors) {
  private val IconSize = 64
  private val CellPadding = 10

  hgrow = Priority.Always

  placeholder = new Label("No apps installed")

  columns ++= Seq(
    new TableColumn[Descriptor, Option[File]] {
      sortable = false

      cellValueFactory = { row =>
        new ReadOnlyObjectWrapper[Option[File]](row.value.iconFile)
      }

      cellFactory = { _: TableColumn[Descriptor, Option[File]] =>
        new TableCell[Descriptor, Option[File]] {
          item.onChange { (_, _, newValue) =>
            if (newValue != null) {
              val iconUrl =
                if (newValue.isDefined) {
                  "file:" + newValue.get.getAbsolutePath
                } else {
                  "/info/gianlucacosta/maanbloem/moondeploy.png"
                }

              val icon = new Image(iconUrl, IconSize, IconSize, true, true)

              graphic = new ImageView(icon)

              padding = Insets(CellPadding)
            }
          }
        }
      }
    },

    new TableColumn[Descriptor, String] {
      text = "Name"
      sortable = true
      prefWidth = 250

      cellValueFactory = { row => new ReadOnlyObjectWrapper[String](row.value.name) }

      cellFactory = createLabelCellFactory()
    },

    new TableColumn[Descriptor, String] {
      text = "Version"
      sortable = true
      prefWidth = 80

      cellValueFactory = { row => new ReadOnlyObjectWrapper[String](row.value.appVersion) }

      cellFactory = createLabelCellFactory()
    },


    new TableColumn[Descriptor, String] {
      text = "Publisher"
      sortable = true
      prefWidth = 250

      cellValueFactory = { row => new ReadOnlyObjectWrapper[String](row.value.publisher) }

      cellFactory = createLabelCellFactory()
    },

    new TableColumn[Descriptor, String] {
      text = "Address"
      sortable = true
      prefWidth = 250

      cellValueFactory = { row => new ReadOnlyObjectWrapper[String](row.value.baseUrl.toString) }

      cellFactory = createLabelCellFactory()
    }
  )


  private def createLabelCellFactory() = { column: TableColumn[Descriptor, String] =>
    new TableCell[Descriptor, String] {
      item.onChange { (_, _, newValue) =>
        if (newValue != null) {
          graphic = new Label(newValue) {
            alignment = Pos.Center
            prefWidth <== column.width
            prefHeight = IconSize + 2 * CellPadding
            wrapText = true
            padding = Insets(CellPadding)
          }
        }
      }
    }
  }
}