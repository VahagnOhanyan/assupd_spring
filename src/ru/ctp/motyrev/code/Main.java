package ru.ctp.motyrev.code;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.ctp.motyrev.controllers.MainController;


public class Main extends Application {
    @FXML
    public static Stage authStage;
    @Override
    public void start(Stage primaryStage) throws Exception {

        authStage = new Stage();

        Parent auth = FXMLLoader.load(getClass().getResource("/ru/ctp/motyrev/fxml/auth.fxml"));
        authStage.setTitle("Вход в систему");
        authStage.setScene(new Scene(auth));
        authStage.setResizable(false);
        authStage.setOnCloseRequest(arg0 -> System.exit(0));

        authStage.showAndWait();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/main.fxml"));
        Parent fxmlMain = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.setMainStage(primaryStage);
        primaryStage.setTitle("АСУ ПД v 1.0.6");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(new Scene(fxmlMain, 1024, 768));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
