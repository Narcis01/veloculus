package com.miele.javafx.config;

import com.miele.veloculus.VeloculusApplication;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * This class configures the JavaFx application
 */
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    /**
     * This method initiates the application
     */
    @Override
    public void init() {

        ApplicationContextInitializer<GenericApplicationContext> initializer =
                ac -> {
                    ac.registerBean(Application.class, () -> JavaFxApplication.this);
                    ac.registerBean(Parameters.class, this::getParameters);
                    ac.registerBean(HostServices.class, this::getHostServices);
                };

        this.context = new SpringApplicationBuilder()
                .sources(VeloculusApplication.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));

    }

    /**
     * This method start the application
     */
    @Override
    public void start(Stage stage) {

        this.context.publishEvent(new StageReadyEvent(stage));
    }
    /**
     * This method stop the application
     */
    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}

