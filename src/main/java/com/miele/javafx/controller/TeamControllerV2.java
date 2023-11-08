package com.miele.javafx.controller;

import com.miele.backend.availability.AvailabilityServiceImp;
import com.miele.backend.daysOff.DaysOffServiceImp;
import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperServiceImp;
import com.miele.backend.migration.MigrationDataImp;
import com.miele.backend.spikes.SpikesServiceImp;
import com.miele.backend.sprint.Sprint;
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
import java.util.Optional;

@Component
@Slf4j
@Setter
public class TeamControllerV2 {

    private DeveloperServiceImp developerServiceImp;
    private TeamServiceImp teamServiceImp;
    private AvailabilityServiceImp availabilityServiceImp;
    private DaysOffServiceImp daysOffServiceImp;
    private SpikesServiceImp spikesServiceImp;
    private TopicsServiceImp topicsServiceImp;
    private SprintServiceImp sprintServiceImp;
    private MigrationDataImp migrationDataImp;

    @FXML
    public TableView teams;
    @FXML
    public Label info;
    @FXML
    public TextField teamInput;


    public void developer(ActionEvent actionEvent) throws IOException {
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

    public void sprint(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SprintSceneV2.fxml"));
        Parent root = loader.load();
        SprintControllerV2 controller = setUpSprintScene(loader.getController());

        /*
        Setup tables and combobox
         */
        controller.teams.getItems().clear();
        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        List<String> teamList = teamServiceImp.getAll().stream().map(Team::getName).toList();
        dataTeam.addAll(teamList);
        controller.teams.setItems(dataTeam);
        ObservableList<Object> dataDev = FXCollections.observableArrayList();

        controller.teams.setOnAction(event -> {
            controller.developerCombo.getItems().clear();
            Team team = teamServiceImp.findByName((String) controller.teams.getValue());
            List<String> developers = developerServiceImp.getByTeam(team).stream().map(Developer::getName).toList();
            dataDev.addAll(developers);
            controller.developerCombo.setItems(dataDev);

            ObservableList<Sprint> data = FXCollections.observableArrayList(sprintServiceImp.getByTeam((String) controller.teams.getValue()));
            controller.sprintTable.getItems().clear();
            controller.sprintTable.getColumns().clear();
            createSprintTable(controller.sprintTable, data);
        });
        ObservableList<Sprint> data = FXCollections.observableArrayList(sprintServiceImp.getAll());
        /*
        Implement table with data
         */
        createSprintTable(controller.sprintTable, data);

        controller.teams.setPromptText("TEAM");
        controller.developerCombo.setPromptText("DEVELOPER");

        changeScene(actionEvent, root);
    }

    public void reset(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Factory reset");
        alert.setHeaderText("Are you sure about it?");
        alert.setContentText("All developers, sprints and teams will be deleted. Make sure you do a backup before you delete the data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            developerServiceImp.removeAll();
            sprintServiceImp.removeAll();
            teamServiceImp.removeAll();

        }

    }

    public void importData(ActionEvent actionEvent) throws IOException {
            /*
                Set up for the next scene's controller
            */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ImportScene.fxml"));
        Parent root = loader.load();
        ImportController controller = setImportScene(loader.getController());

        /*
         * ComboBox for team config
         * getting data from db and convert it into ObservableList and adding to comboBox
         */
        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        controller.teams.getItems().clear();
        List<String> names = teamServiceImp.getAll().stream().map(Team::getName).toList();
        dataTeam.addAll(names);

        controller.teams.setItems(dataTeam);
        controller.sprints.setItems(dataTeam);

        controller.teams.setPromptText("TEAM");
        controller.sprints.setPromptText("SPRINT");

        /*
            Method to change scene
         */
        changeScene(actionEvent, root);
    }

    public void export(ActionEvent actionEvent) throws IOException {
        /*
            Set up for the next scene's controller
        */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ExportScene.fxml"));
        Parent root = loader.load();
        ExportController controller = setExportScene(loader.getController());

        /*
         * ComboBox for team config
         * getting data from db and convert it into ObservableList and adding to comboBox
         */
        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        controller.comboTeam.getItems().clear();
        List<Team> teamList = teamServiceImp.getAll();
        dataTeam.addAll(teamList);

        controller.comboTeam.setItems(dataTeam);
        controller.comboDev.setItems(dataTeam);

        controller.comboTeam.setPromptText("TEAM");
        controller.comboDev.setPromptText("DEVELOPER");

        /*
            Method to change scene
         */
        changeScene(actionEvent, root);
    }

    public void createTeam(ActionEvent actionEvent) {
        if (teamInput.getText().isEmpty() || teamInput.getText().isBlank()) {
            info.setText("Enter a valid name!");
        } else if (teamServiceImp.findByName(teamInput.getText().toUpperCase()) != null) {
            info.setText("Team already exists!");
        } else {
            Team team = Team.builder().name(teamInput.getText().toUpperCase()).build();
            teamServiceImp.save(team);

            teams.getColumns().clear();
            ObservableList<Team> data = FXCollections.observableArrayList(teamServiceImp.getAll());
            createTeamTable(teams,data);

            info.setText("Team " + teamInput.getText().toUpperCase() + " created!");
        }
    }

    private void createSprintTable(TableView tableView, ObservableList<Sprint> data) {
        tableView.setItems(data);
        TableColumn<Sprint, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setSortable(false);

        TableColumn<Sprint, String> date = new TableColumn<>("DATE");
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        date.setSortable(false);

        TableColumn<Sprint, Double> vel = new TableColumn<>("VELOCITY");
        vel.setCellValueFactory(new PropertyValueFactory<>("velocity"));
        vel.setSortable(false);

        TableColumn<Sprint, Integer> average = new TableColumn<>("AVERAGE");
        average.setCellValueFactory(new PropertyValueFactory<>("average"));
        average.setSortable(false);
        TableColumn<Sprint, Double> workingMandays = new TableColumn<>("Working Mandays");
        workingMandays.setCellValueFactory(new PropertyValueFactory<>("workingMandays"));
        workingMandays.setSortable(false);

        TableColumn<Sprint, Integer> estimatedVel = new TableColumn<>("ESTIMATED VELOCITY");
        estimatedVel.setCellValueFactory(new PropertyValueFactory<>("estimatedVelocity"));
        estimatedVel.setSortable(false);

        TableColumn<Sprint, Double> ptsDevDay = new TableColumn<>("POINTS DEV DAY");
        ptsDevDay.setCellValueFactory(new PropertyValueFactory<>("ptsDevDay"));
        ptsDevDay.setSortable(false);

        TableColumn<Sprint, Double> averagePtsDevDay = new TableColumn<>("AVERAGE POINTS DEV DAY");
        averagePtsDevDay.setCellValueFactory(new PropertyValueFactory<>("averagePtsDevDay"));
        averagePtsDevDay.setSortable(false);

        TableColumn<Sprint, Double> developers = new TableColumn<>("DEVELOPERS");
        developers.setCellValueFactory(new PropertyValueFactory<>("developers"));
        developers.setSortable(false);

        TableColumn<Sprint, String> team = new TableColumn<>("TEAM");
        team.setCellValueFactory(new PropertyValueFactory<>("team"));
        team.setSortable(false);

        TableColumn<Sprint, Void> cellBtn = new TableColumn<>("Delete");
        Callback<TableColumn<Sprint, Void>, TableCell<Sprint, Void>> cellFactory = new Callback<TableColumn<Sprint, Void>, TableCell<Sprint, Void>>() {
            @Override
            public TableCell<Sprint, Void> call(final TableColumn<Sprint, Void> param) {
                final TableCell<Sprint, Void> cell = new TableCell<Sprint, Void>() {

                    private final Button btn = new Button("X");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Sprint data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            sprintServiceImp.removeSprint(data);
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
        tableView.getColumns().addAll(cellBtn, id, date, vel, workingMandays, average, developers, team, estimatedVel, ptsDevDay);

    }


    private void createDeveloperTable(TableView tableView, ObservableList<Developer> data) {
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
        tableView.getColumns().addAll(cellBtn, id, name, team);
    }

    private void createTeamTable(TableView tableView, ObservableList<Team> data) {
        tableView.setItems(data);
        TableColumn<Team, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setSortable(false);

        TableColumn<Team, String> date = new TableColumn<>("DATE");
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        date.setSortable(false);

        TableColumn<Team, String> name = new TableColumn<>("NAME");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setSortable(false);

        TableColumn<Team, Void> cellBtn = new TableColumn<>("Delete");
        Callback<TableColumn<Team, Void>, TableCell<Team, Void>> cellFactory = new Callback<TableColumn<Team, Void>, TableCell<Team, Void>>() {
            @Override
            public TableCell<Team, Void> call(final TableColumn<Team, Void> param) {
                final TableCell<Team, Void> cell = new TableCell<Team, Void>() {

                    private final Button btn = new Button("X");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Team data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            teamServiceImp.delete(data.getName());
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
        tableView.getColumns().addAll(cellBtn, id, name);
    }

    private DeveloperControllerV2 setUpDeveloperScene(DeveloperControllerV2 controller) {
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

    private SprintControllerV2 setUpSprintScene(SprintControllerV2 controller) {
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

    private ImportController setImportScene(ImportController controller) {
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

    private ExportController setExportScene(ExportController controller) {
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
