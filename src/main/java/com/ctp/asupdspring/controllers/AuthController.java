package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.SystemBuildService;
import com.ctp.asupdspring.app.repo.UserInfoRepository;
import com.ctp.asupdspring.app.repo.UserRepository;
import com.ctp.asupdspring.app.repo.UserRoleRepository;
import com.ctp.asupdspring.domain.UserEntity;
import com.ctp.asupdspring.domain.UserInfoEntity;
import com.ctp.asupdspring.fw.Main;
import com.ctp.asupdspring.fw.UserAuthDetails;
import com.ctp.asupdspring.fw.UserAuthDetailsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.ctp.asupdspring.controllers.AlertController.AlertType.INFO;

@RequiredArgsConstructor
@Component
@FxmlView("auth.fxml")
public class AuthController {
    private final FxWeaver fxWeaver;
    private final SystemBuildService systemBuildService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserAuthDetailsService userAuthDetailsService;
    FxControllerAndView<AlertController, VBox> alertDialog;

    @FXML
    private TextField fldLogin;
    @FXML
    private PasswordField fldPass;
    @FXML
    private ImageView logoView;
    private final UserDetailsService userDetailsService;
    String userprofile = System.getenv("USERPROFILE");

    private static String host;
    private static String port;
    private static String dbName;

    @FXML
    private void initialize() {

        fldPass.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {

                saveLogin();
                authentication();
            }
        });

     /*   Image im = new Image("src/main/resources/static/images/logo.png");
        logoView.setImage(im);*/

        InputStream inp;
        Properties props = new Properties();

        try {
            inp = new FileInputStream(userprofile + "\\ASUPD\\config\\userLogin.ini");
            props.load(inp);
            if (props.getProperty("user_login") != null) {
                fldLogin.setText(props.getProperty("user_login"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionButtonPressed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        if (!(source instanceof Button)) {
            return;
        }

        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "btnLogin":
                saveLogin();
                authentication();
                break;

            case "btnExit":
                System.exit(0);
                break;
        }
    }

    private void saveLogin() {

        File propDir = new File(userprofile + "\\ASUPD\\config");

        if (!propDir.exists()) {
            try {
                propDir.mkdirs();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }

        try {
            Properties properties = new Properties();
            properties.setProperty("user_login", fldLogin.getText());

            File file = new File(userprofile + "\\ASUPD\\config\\userLogin.ini");
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Autologin");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authentication() {

        if (!systemBuildService.getSystemBuildVersion().equals("")) {
            if (!fldLogin.getText().equals("") && !fldPass.getText().equals("")) {
                Optional<UserInfoEntity> userInfoEntity = userInfoRepository.findByUserLoginAndUserPass(fldLogin.getText(), fldPass.getText());
                if (userInfoEntity.isPresent()) {
                    Optional<UserEntity> userEntity = userRepository.findByUserInfoId(userInfoEntity.get());
                    if (userEntity.isPresent()) {
                        initRoles(fldLogin.getText());
                        //   userAuthDetailsService.loadUserByUsername(fldLogin.getText());
                        MainController.role = userInfoEntity.get().getUserRoleId().getUserRoleName();
                        MainController.authorities = ContextController.authorities;
                        MainController.who = ContextController.who;
                        Main.authStage.close();
                    } else {
                        setAlertStage("Ошибка аутентификации", null, "Проверьте правильность ввода данных для аутентификации", INFO);
                    }
                }
            } else {
                setAlertStage("Ошибка аутентификации", null, "Введите данные для аутентификации", INFO);
            }
        } else {
            setAlertStage("Ошибка аутентификации", null,
                    "Версия клиента не совпадает с версией базы данных. Обратитесь к системному администратору для обновления ПО.", INFO);
        }
    }

    public void initRoles(String login) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        ContextController.authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toMap(k -> {
                    int i = k.indexOf(":");
                    if (i < 0) {
                        if (k.equalsIgnoreCase("super_user")) {
                            return k;
                        }
                    } else {
                        System.out.println(i);
                        return k.substring(0, i);
                    }
                    return k;
                }, k -> {

                    int i = k.indexOf(":");
                    if (i < 0) {
                        if (k.equalsIgnoreCase("super_user")) {
                            return 1;
                        }
                    } else {
                        return Integer.parseInt(k.substring(i + 1));
                    }
                    return 4;
                }));
        UserAuthDetails userAuthDetails = (UserAuthDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ContextController.who = userAuthDetails.getUsername();
        System.out.println("ContextController.who: " + ContextController.who);
    }

    private void setAlertStage(String title, String header, String content, AlertController.AlertType type) {
        alertDialog = fxWeaver.load(AlertController.class);
        AlertController controller = alertDialog.getController();
        controller.setTitle(title);
        controller.setHeader(header);
        controller.setContent(content);
        controller.show();
    }
}