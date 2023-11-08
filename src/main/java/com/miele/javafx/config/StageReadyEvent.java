package com.miele.javafx.config;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * This class creates a new stage
 */
public class StageReadyEvent extends ApplicationEvent {
    /**
     * @return The object on which the Event initially occurred
     */
    public Stage getStage() {
        return (Stage) getSource();
    }

    /**
     * Constructor
     */
    public StageReadyEvent(Stage source) {
        super(source);
    }
}
