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
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.lang.String.valueOf;

@Component
@Slf4j
@Setter
public class SprintChartController {

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
    public LineChart chart;
    @FXML
    public CategoryAxis xAxis;
    @FXML
    public NumberAxis yAxis;

    public void back(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SprintSceneV2.fxml"));
        Parent root = loader.load();
        SprintControllerV2 controller = setUpSprintScene(loader.getController());
        controller.infoLabel.setText("");
        controller.developerCombo.setPromptText("DEVELOPER");
        controller.teams.setPromptText("TEAM");
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
            createSprintTable(controller.sprintTable,data);
        });
        ObservableList<Sprint> data = FXCollections.observableArrayList(sprintServiceImp.getAll());
        /*
        Implement table with data
         */
        createSprintTable(controller.sprintTable,data);

        changeScene(actionEvent,root);
    }

    public void submit(ActionEvent actionEvent) {
        Team team = teamServiceImp.findByName(teams.getValue().toString());
        List<Sprint> sprints = sprintServiceImp.getByTeamAndOrderByDate(team.getName());
        chart.getData().clear();
        xAxis.setLabel("Data");
        yAxis.setLabel("Velocity");
        chart.setTitle("Sprint Charts");

        XYChart.Series data = new XYChart.Series();

        for (Sprint sprint : sprints) {
            data.getData().add(new XYChart.Data<>(sprint.getDate().toString(),sprint.getVelocity()));

        }

        data.setName(team.getName());
        chart.getData().add(data);
    }

    private void createSprintTable(TableView tableView , ObservableList<Sprint> data ){
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
        tableView.getColumns().addAll(cellBtn,id,date,vel,workingMandays,average,developers,team,estimatedVel,ptsDevDay);

    }
    private void changeScene(ActionEvent actionEvent, Parent root) {

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.resizableProperty().set(false);
        stage.show();

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
}