package com.miele.javafx.controller;

import com.miele.backend.availability.Availability;
import com.miele.backend.availability.AvailabilityServiceImp;
import com.miele.backend.daysOff.DaysOff;
import com.miele.backend.daysOff.DaysOffServiceImp;
import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperServiceImp;
import com.miele.backend.migration.MigrationDataImp;
import com.miele.backend.spikes.Spikes;
import com.miele.backend.spikes.SpikesServiceImp;
import com.miele.backend.sprint.SprintServiceImp;
import com.miele.backend.team.Team;
import com.miele.backend.team.TeamServiceImp;
import com.miele.backend.topics.Topics;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Setter
@Slf4j
public class SprintControllerV2 {

    private DeveloperServiceImp developerServiceImp;
    private TeamServiceImp teamServiceImp;
    private AvailabilityServiceImp availabilityServiceImp;
    private DaysOffServiceImp daysOffServiceImp;
    private SpikesServiceImp spikesServiceImp;
    private TopicsServiceImp topicsServiceImp;
    private SprintServiceImp sprintServiceImp;
    private MigrationDataImp migrationDataImp;
    @FXML
    public ComboBox teams;
    @FXML
    public ComboBox developerCombo;
    @FXML
    public TextField topics;
    @FXML
    public TextField availability;
    @FXML
    public TextField daysOff;
    @FXML
    public TextField spikes;
    @FXML
    public Label infoLabel;
    @FXML
    public TableView sprintTable;

    @Autowired
    public SprintControllerV2(DeveloperServiceImp developerServiceImp,
                              TeamServiceImp teamServiceImp,
                              AvailabilityServiceImp availabilityServiceImp,
                              DaysOffServiceImp daysOffServiceImp,
                              SpikesServiceImp spikesServiceImp,
                              TopicsServiceImp topicsServiceImp,
                              SprintServiceImp sprintServiceImp,
                              MigrationDataImp migrationDataImp) {
        this.developerServiceImp = developerServiceImp;
        this.teamServiceImp = teamServiceImp;
        this.availabilityServiceImp = availabilityServiceImp;
        this.daysOffServiceImp = daysOffServiceImp;
        this.spikesServiceImp = spikesServiceImp;
        this.topicsServiceImp = topicsServiceImp;
        this.sprintServiceImp = sprintServiceImp;
        this.migrationDataImp = migrationDataImp;

    }

    public SprintControllerV2() {
    }

    public void developer(ActionEvent actionEvent) throws IOException {

        /*
        Set up for the next scene's controller
         */

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/DeveloperSceneV2.fxml"));
        Parent root = loader.load();
        DeveloperControllerV2 controller = setUpDeveloperScene(loader.getController());
        controller.teams.setPromptText("TEAM");


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

        /*
            Method to change scene
         */
        changeScene(actionEvent, root);


    }

    public void team(ActionEvent actionEvent) throws IOException {
        /*
        Set up for the next scene's controller
         */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/TeamSceneV2.fxml"));
        Parent root = loader.load();
        TeamControllerV2 controller = setUpTeamScene(loader.getController());

        /*
            Set up for table
         */
        ObservableList<Team> teams = FXCollections.observableArrayList();
        List<Team> teamsName = teamServiceImp.getAll();
        teams.addAll(teamsName);
        createTeamTable(controller.teams, teams);

        /*
            Method to change scene
         */
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


        controller.teams.setPromptText("TEAMS");
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
        List<String> teamList = teamServiceImp.getAll().stream().map(Team::getName).toList();
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

    public void saveDataForDeveloper(ActionEvent actionEvent) {

        Availability av = Availability.builder().availabilityPercent(Integer.parseInt(availability.getText()))
                .date(LocalDate.now()).build();
        DaysOff days = DaysOff.builder().daysOff(Integer.parseInt(daysOff.getText())).date(LocalDate.now()).build();
        Spikes spike = Spikes.builder().spikes(Integer.parseInt(spikes.getText())).date(LocalDate.now()).build();
        Topics topic = Topics.builder().topics(topics.getText()).date(LocalDate.now()).build();
        Team team = teamServiceImp.findByName((String) teams.getValue());
        Developer developer = developerServiceImp.getByNameAndTeam((String) developerCombo.getValue(), team);
        developer.addSpikes(spike);
        developer.addTopics(topic);
        developer.addAvailability(av);
        developer.addDaysOff(days);


        availabilityServiceImp.save(av);
        daysOffServiceImp.save(days);
        spikesServiceImp.save(spike);
        topicsServiceImp.save(topic);
        developerServiceImp.save(developer);
        infoLabel.setText("You just added info for " + developer.getName());
        log.info(developerServiceImp.getAll().toString());

    }

    public void chart(ActionEvent actionEvent) throws IOException {

        /*
            Set up for the next scene's controller
        */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SprintChart.fxml"));
        Parent root = loader.load();
        SprintChartController controller = setUpChartScene(loader.getController());

        /*
         * ComboBox for team config
         * getting data from db and convert it into ObservableList and adding to comboBox
         */
        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        controller.teams.getItems().clear();
        List<Team> teamList = teamServiceImp.getAll();
        for (Team team : teamList) {
            dataTeam.add(team.getName());
        }
        controller.teams.setItems(dataTeam);

        controller.teams.setPromptText("TEAM");
        /*
            Method to change scene
         */
        changeScene(actionEvent, root);
    }


    public void saveSprint(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SprintCalculatorScene.fxml"));
        Parent root = loader.load();
        SprintCalculatorController controller = setSaveSprintScene(loader.getController());

        ObservableList<Object> dataTeam = FXCollections.observableArrayList();
        List<String> teams = teamServiceImp.getAll().stream().map(Team::getName).toList();
        dataTeam.addAll(teams);
        controller.comboTeam.setItems(dataTeam);
        controller.comboTeam.setPromptText("TEAM");
        changeScene(actionEvent,root);
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

    private TeamControllerV2 setUpTeamScene(TeamControllerV2 controller) throws IOException {
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

    private SprintChartController setUpChartScene(SprintChartController controller) {
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

    private SprintCalculatorController setSaveSprintScene(SprintCalculatorController controller) {
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
