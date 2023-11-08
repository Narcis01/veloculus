package com.miele.javafx.controller;


import com.miele.backend.availability.AvailabilityServiceImp;
import com.miele.backend.daysOff.DaysOffServiceImp;
import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperServiceImp;
import com.miele.backend.migration.MigrationDataImp;
import com.miele.backend.spikes.SpikesServiceImp;
import com.miele.backend.sprint.SprintServiceImp;
import com.miele.backend.team.Team;
import com.miele.backend.team.TeamServiceImp;
import com.miele.backend.topics.TopicsServiceImp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Controller for update developer
 */
@Component
@Slf4j
@Setter
public class UpdateDeveloperController {

    private DeveloperServiceImp developerServiceImp;
    private TeamServiceImp teamServiceImp;
    private AvailabilityServiceImp availabilityServiceImp;
    private DaysOffServiceImp daysOffServiceImp;
    private SpikesServiceImp spikesServiceImp;
    private TopicsServiceImp topicsServiceImp;
    private SprintServiceImp sprintServiceImp;
    private MigrationDataImp migrationDataImp;

    @FXML
    public ComboBox comboTeam;
    @FXML
    public ComboBox comboDev;
    @FXML
    private TextField newName;



    /**
     * Method to go back to main menu
     */
    public void cancel(ActionEvent actionEvent) throws IOException {


        /*
        Set up for the next scene's controller
         */

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/DeveloperSceneV2.fxml"));
        Parent root = loader.load();
        DeveloperControllerV2 controller = setUpDeveloperScene(loader.getController());


        /*
            Set up for comboBox and action for table
         */
        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        List<String> teams = teamServiceImp.getAll().stream().map(Team::getName).toList();
        dataTeam.addAll(teams);
        controller.teams.setItems(dataTeam);

        controller.teams.setOnAction(event -> {
            controller.developerTable.getItems().clear();
            controller.developerTable.getColumns().clear();
            Team team = teamServiceImp.findByName((String) controller.teams.getValue());
            List<Developer> developers = developerServiceImp.getByTeam(team);
            ObservableList<Developer> data = FXCollections.observableArrayList(developers);
            createDeveloperTable(controller.developerTable, data);
        });
        /*
            Set up for default table
         */
        ObservableList<Developer> data = FXCollections.observableArrayList(developerServiceImp.getAll());
        createDeveloperTable(controller.developerTable, data);

        controller.teams.setPromptText("TEAM");

        /*
            Method to change scene
         */
        changeScene(actionEvent, root);
    }

    /**
     * Method to update developer
     */
    public void submit(ActionEvent actionEvent) throws IOException {

        Developer developer = developerServiceImp.getByName((String) comboDev.getValue());

        if (newName.getText() != "") {
            developer.setName(newName.getText().toUpperCase());
        }
        if (comboTeam.getValue() != null) {
            Team team = teamServiceImp.findByName((String) comboTeam.getValue());
            team.addDeveloper(developer);
            teamServiceImp.save(team);
        }

        developerServiceImp.save(developer);


        /*
        Set up for the next scene's controller
         */

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/DeveloperSceneV2.fxml"));
        Parent root = loader.load();
        DeveloperControllerV2 controller = setUpDeveloperScene(loader.getController());


        /*
            Set up for comboBox and action for table
         */
        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        List<String> teams = teamServiceImp.getAll().stream().map(Team::getName).toList();
        dataTeam.addAll(teams);
        controller.teams.setItems(dataTeam);

        controller.teams.setOnAction(event -> {
            controller.developerTable.getItems().clear();
            controller.developerTable.getColumns().clear();
            Team team = teamServiceImp.findByName((String) controller.teams.getValue());
            List<Developer> developers = developerServiceImp.getByTeam(team);
            ObservableList<Developer> data = FXCollections.observableArrayList(developers);
            createDeveloperTable(controller.developerTable, data);
        });
        /*
            Set up for default table
         */
        ObservableList<Developer> data = FXCollections.observableArrayList(developerServiceImp.getAll());
        createDeveloperTable(controller.developerTable, data);

        controller.teams.setPromptText("TEAM");

        /*
            Method to change scene
         */
        changeScene(actionEvent, root);

    }

    private void createDeveloperTable(TableView tableView, ObservableList<Developer> data ){
        tableView.setItems(data);
        TableColumn<Developer, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setSortable(false);

        TableColumn<Developer, String> date = new TableColumn<>("DATE");
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        date.setSortable(false);

        TableColumn<Developer, Double> name = new TableColumn<>("NAME");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setSortable(false);


        TableColumn<Developer, String> team = new TableColumn<>("TEAM");
        team.setCellValueFactory(new PropertyValueFactory<>("team"));
        team.setSortable(false);

        TableColumn<Developer, Void> cellBtn = new TableColumn<>("Delete");
        Callback<TableColumn<Developer, Void>, TableCell<Developer, Void>> cellFactory = new Callback<TableColumn<Developer, Void>, TableCell<Developer, Void>>() {
            @Override
            public TableCell<Developer, Void> call(final TableColumn<Developer, Void> param) {
                final TableCell<Developer, Void> cell = new TableCell<Developer, Void>() {

                    private final Button btn = new Button("X");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Developer data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            developerServiceImp.deleteByEntity(data);
                            getTableView().getItems().remove(data);
                        });
                        btn.getStyleClass().add("tableButton");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        cellBtn.setCellFactory(cellFactory);
        cellBtn.setSortable(false);
        tableView.getColumns().addAll(cellBtn,id,name,team);
    }

    private DeveloperControllerV2 setUpDeveloperScene(DeveloperControllerV2 controller) throws IOException {
        controller.setDeveloperServiceImp(developerServiceImp);
        controller.setTeamServiceImp(teamServiceImp);
        controller.setAvailabilityServiceImp(availabilityServiceImp);
        controller.setDaysOffServiceImp(daysOffServiceImp);
        controller.setSpikesServiceImp(spikesServiceImp);
        controller.setTopicsServiceImp(topicsServiceImp);
        controller.setSprintServiceImp(sprintServiceImp);
        controller.setMigrationDataImp(migrationDataImp);

        return controller;
    }

    private void changeScene(ActionEvent actionEvent, Parent root) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.resizableProperty().set(false);
        stage.show();

    }
}
