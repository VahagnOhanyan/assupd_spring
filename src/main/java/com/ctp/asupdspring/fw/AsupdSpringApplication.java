package com.ctp.asupdspring.fw;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.ctp.asupdspring")
@EnableJpaRepositories(basePackages = "com.ctp.asupdspring.app.repo")
@EntityScan(basePackages = "com.ctp.asupdspring.domain")
public class AsupdSpringApplication{

    public static void main(String[] args) {

        Application.launch(Main.class, args);
    }


}
