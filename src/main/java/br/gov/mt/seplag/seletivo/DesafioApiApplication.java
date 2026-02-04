package br.gov.mt.seplag.seletivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class DesafioApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesafioApiApplication.class, args);
    }
}
