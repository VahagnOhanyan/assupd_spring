package com.ctp.asupdspring.fw;

import com.ctp.asupdspring.controllers.AuthController;
import com.ctp.asupdspring.controllers.FxmlViewAccessController;
import com.ctp.asupdspring.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class Main extends Application {
    private ConfigurableApplicationContext applicationContext;
    @FXML
    public static Stage authStage;
    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(AsupdSpringApplication.class)
                .run(args);

        initSecurity();
    }
    @Override
    public void start(Stage stage) throws Exception {
        authStage = new Stage();
        FxWeaver fxWeaver0 = applicationContext.getBean(FxWeaver.class);
        Parent auth = fxWeaver0.loadView(AuthController.class);
        authStage.setTitle("Вход в систему");
        authStage.setScene(new Scene(auth));
        authStage.setResizable(false);
        authStage.setOnCloseRequest(arg0 -> System.exit(0));
        authStage.showAndWait();

        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent parent = fxWeaver.loadView(MainController.class);
        Optional<Node> nodeOptional = fxWeaver.load(MainController.class).getView();

        if(nodeOptional.isPresent()){
            FxmlViewAccessController.resolveAccess(parent);
       }
        stage.setTitle("АСУ ПД v 1.0.6");
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setScene(new Scene(parent,1024, 768));
        stage.show();
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    public static void initSecurity() {
        SecurityContextHolder.setStrategyName("MODE_GLOBAL");
    }


}
