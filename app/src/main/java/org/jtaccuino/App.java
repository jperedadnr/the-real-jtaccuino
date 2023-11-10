/*
 * Copyright 2024 JTaccuino Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jtaccuino;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import static org.jtaccuino.UiUtils.createSVGToolbarButton;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final String TITLE = "JTaccuino - A scientifc notebook powered by Java";

    private Scene scene;
    private BorderPane bp;
    private Sheet sheet;

    @Override
    public void start(Stage stage) throws Exception {
        bp = new BorderPane();
        sheet = Sheet.of();
        bp.setCenter(sheet);
        bp.setTop(createMainToolBar(stage));
        scene = new Scene(bp);
        scene.getStylesheets().add(this.getClass().getResource("/jtaccuino.css").toExternalForm());
        stage.setScene(scene);
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() * 0.75);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth() * 0.6);
        stage.setTitle(TITLE);
        stage.setOnCloseRequest((event) -> {
            if (null != sheet) {
                sheet.close();
            }
        });
        stage.show();
    }

    private void activateNotebook(Notebook notebook) {
        sheet.close();
        bp.setCenter(null);
        if (null != notebook) {
            sheet = Sheet.of(notebook);
        } else {
            sheet = Sheet.of();
        }
        bp.setCenter(sheet);
    }

    private HBox createMainToolBar(Stage stage) {
        var empty = createSVGToolbarButton("empty-notebook", "Empty Notebook", "main-toolbar-button");
        empty.setOnAction((event) -> {
            activateNotebook(null);
        });

        var load = createSVGToolbarButton("load-notebook", "Load Notebook", "main-toolbar-button");
        load.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Notebook File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Notebook Files", "*.ipynb"),
                    new ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                System.out.println(selectedFile);
                try {
                    Jsonb jsonb = JsonbBuilder.create();
                    var notebook = jsonb.fromJson(new FileReader(selectedFile), Notebook.class);
                    jsonb.close();
                    activateNotebook(notebook);
                } catch (final Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        });
        var save = createSVGToolbarButton("save-notebook", "Save Notebook", "main-toolbar-button");
        save.setOnAction((event) -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Notebook File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Notebook Files", "*.ipynb"),
                    new ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showSaveDialog(stage);
            var config = new JsonbConfig();
            config.setProperty(JsonbConfig.FORMATTING, true);
            Jsonb jsonb = JsonbBuilder.create(config);
            try {
                jsonb.toJson(sheet.toNotebook(), new FileWriter(selectedFile));
                jsonb.close();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        });

        var execute = createSVGToolbarButton("execute-notebook", "Execute Notebook", "main-toolbar-button");
        execute.setOnAction((event) -> {
            sheet.execute();
        });
        var resetAndExecute = createSVGToolbarButton("reset-execute-notebook", "Reset Shell And Execute Notebook", "main-toolbar-button");
        resetAndExecute.setOnAction((event) -> {
            sheet.resetAndExecute();
        });
        var toolbar = new HBox(empty, load, save, execute, resetAndExecute);
        HBox.setHgrow(toolbar, Priority.NEVER);
        toolbar.maxWidthProperty().bind(toolbar.prefWidthProperty());
        toolbar.getStyleClass().add("main-toolbar");
        return toolbar;
    }

}
