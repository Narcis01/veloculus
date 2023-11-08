package com.miele.javafx.config;

import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperServiceImp;
import com.miele.backend.sprint.Sprint;
import com.miele.backend.sprint.SprintServiceImp;
import com.miele.backend.team.Team;
import com.miele.backend.team.TeamServiceImp;
import com.miele.javafx.controller.SprintControllerV2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class StageListener implements ApplicationListener<StageReadyEvent> {

    private final String appTitle;
    private final Resource fxml;
    private final ApplicationContext ac;
    private final TeamServiceImp teamServiceImp;
    private final DeveloperServiceImp developerServiceImp;
    private final SprintServiceImp sprintServiceImp;

    public StageListener(
            @Value("${spring.application.ui.title}") String appTitle,
            @Value("classpath:/FXML/SprintSceneV2.fxml") Resource fxml,
            ApplicationContext ac, TeamServiceImp teamServiceImp, DeveloperServiceImp developerServiceImp, SprintServiceImp sprintServiceImp) {
        this.appTitle = appTitle;
        this.fxml = fxml;
        this.ac = ac;
        this.teamServiceImp = teamServiceImp;
        this.developerServiceImp = developerServiceImp;
        this.sprintServiceImp = sprintServiceImp;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            URL url = this.fxml.getURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setControllerFactory(ac::getBean);
            Parent root = fxmlLoader.load();
            stage.getIcons().add(new Image("icon.png"));
            stage.getIcons().set(0, new Image("icon.png"));
            /*
            Set up for ComboBox
             */
            SprintControllerV2 controller = fxmlLoader.getController();
            controller.teams.getItems().clear();
            ObservableList<Object> dataTeam = FXCollections.observableArrayList();
            List<String> teamList = teamServiceImp.getAll().stream().map(Team::getName).toList();
            dataTeam.addAll(teamList);
            controller.teams.setItems(dataTeam);
            controller.teams.setPromptText("TEAM");
            controller.developerCombo.setPromptText("DEVELOPER");

            /*
            Interaction between ComboBoxes and Tableview
             */
            ObservableList<Object> dataDev = FXCollections.observableArrayList();
            controller.teams.setOnAction(actionEvent -> {
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

            /*
            Set up for default table
             */
            ObservableList<Sprint> data = FXCollections.observableArrayList(sprintServiceImp.getAll());
            createSprintTable(controller.sprintTable, data);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(this.appTitle);
            stage.resizableProperty().set(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

}
