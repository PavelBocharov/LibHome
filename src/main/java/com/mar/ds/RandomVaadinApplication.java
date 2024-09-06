package com.mar.ds;

import com.vaadin.flow.component.dependency.NpmPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NpmPackage(value = "@babel/plugin-proposal-object-rest-spread", version = "^7.20.7")
public class RandomVaadinApplication {

	public static void main(String[] args) {
		SpringApplication.run(RandomVaadinApplication.class, args);
	}

}
