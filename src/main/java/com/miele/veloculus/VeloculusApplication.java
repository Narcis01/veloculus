package com.miele.veloculus;


import com.miele.javafx.config.JavaFxApplication;
import javafx.application.Application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication(scanBasePackages = {"com.miele.backend", "com.miele.javafx"})
@EntityScan("com.miele.backend")
@EnableJpaRepositories("com.miele.backend")
@Slf4j
@RequiredArgsConstructor
public class VeloculusApplication {


    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}
