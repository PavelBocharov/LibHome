package com.mar.ds;

import com.vaadin.flow.component.dependency.NpmPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.mar")
@NpmPackage(value = "@babel/plugin-proposal-object-rest-spread", version = "^7.20.7")
public class LibUIApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibUIApplication.class, args);
    }

}
