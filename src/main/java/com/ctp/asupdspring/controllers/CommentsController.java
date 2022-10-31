package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.StageNoteRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

@RequiredArgsConstructor
@Component
@FxmlView("comments")
public class CommentsController {

    @FXML
    private Label lblTask;
    @FXML
    private Label lblStage;
    @FXML
    private Button copyButton;
    @FXML
    private TableView commentsView;

    private String tableColName;
    private List<Object[]> sql;

    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private final StageNoteRepository stageNoteRepository;

    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();

    String exemp = "";

    @FXML
    private void initialize() {

    }

    public void copyToClip() {

        if (!commentsView.getSelectionModel().isEmpty()) {
            for (int i = 0; i <= data.size() - 1; i++) {
                if (data.get(i) == commentsView.getSelectionModel().getSelectedItem()) {
                    exemp = (String) data.get(i).get(1);
                }
            }

            content.putString(exemp);
            clipboard.setContent(content);
        } else {
          /*  infoAlert.setTitle("Не выбран комментарий");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите комментарий и повторите попытку");
            infoAlert.showAndWait();*/
        }
    }

    public void addData(String task, String stage, String user) {

        formClear();

        lblTask.setTextFill(Color.BLUE);
        lblTask.setText("Задача: " + task);
        lblStage.setTextFill(Color.BLUE);
        lblStage.setText("Этап: " + stage);

        if (!task.equals("Не проектная")) {
            sql = stageNoteRepository.getUserTaskStageComments(task,stage,user.substring(1, user.indexOf(",")));
        } else {
            sql = stageNoteRepository.getUserStageComments(stage,user.substring(1, user.indexOf(",")));
        }
        tableGenerator(sql);
    }

    private void tableGenerator(List<Object[]> sql) {
        commentsView.getSelectionModel().clearSelection();
        commentsView.getColumns().clear();
        commentsView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        data.clear();


        try {
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int j = i;
                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);

                tableColumn.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
                                (String) param.getValue().get(j)));

                tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                    public TableCell call(TableColumn param) {

                        return new TableCell<ObservableList, String>() {

                            private final Text newText;

                            {
                                newText = new Text();
                                newText.wrappingWidthProperty().bind(tableColumn.widthProperty());
                            }

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                setPrefHeight(Control.USE_COMPUTED_SIZE);

                                if (empty || item == null) {
                                    newText.setText("");
                                    setGraphic(newText);
                                    setStyle("");
                                } else {
                                    newText.setText(item);
                                    setGraphic(newText);
                                }
                            }
                        };
                    }
                });

                commentsView.getColumns().addAll(tableColumn);
            }
            //наполнение observableList данными из базы
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }

            //Добавление данных в TableView

            commentsView.setItems(data);

            commentsView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateColName(String bdColName) {
        switch (bdColName) {
            case "to_char":
                tableColName = "Дата";
                break;
            case "stage_note_text":
                tableColName = "Комментарий";
                break;
            case "user_fullname":
                tableColName = "Пользователь";
                break;
            case "user_stage_note_text":
                tableColName = "Комментарий";
                break;
        }
    }

    private void formClear() {
        commentsView.getColumns().clear();
        data.clear();
        sql = "";
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}