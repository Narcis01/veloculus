package com.miele.backend.migration;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miele.backend.availability.AvailabilityServiceImp;
import com.miele.backend.daysOff.DaysOffServiceImp;
import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperServiceImp;
import com.miele.backend.spikes.SpikesServiceImp;
import com.miele.backend.sprint.Sprint;
import com.miele.backend.sprint.SprintServiceImp;
import com.miele.backend.team.Team;
import com.miele.backend.team.TeamServiceImp;
import com.miele.backend.topics.TopicsServiceImp;
import javafx.stage.FileChooser;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * This class is used for import/export data in JSON format
 */

@Service
@Component
@Slf4j
@RequiredArgsConstructor
@Builder
public class MigrationDataImp implements MigrationData {

    private final SprintServiceImp sprintServiceImp;
    private final DeveloperServiceImp developerServiceImp;
    private final TeamServiceImp teamServiceImp;
    private final AvailabilityServiceImp availabilityServiceImp;
    private final DaysOffServiceImp daysOffServiceImp;
    private final SpikesServiceImp spikesServiceImp;
    private final TopicsServiceImp topicsServiceImp;

    /**
     * Export sprints for a specific team
     *
     * @param teamName team name
     */

    @Override
    public void exportSprints(String teamName) throws IOException {
        Path pathDocuments = Paths.get(System.getProperty("user.home"), "documents");
        Path outputFolder = pathDocuments.resolve("Veloculus-output");
        if (!Files.exists(outputFolder)) {
            Files.createDirectory(outputFolder);
        }

        Path outputPath = outputFolder.resolve(teamName + " Sprints-" + LocalDate.now() + ".json");

        List<Sprint> sprints = sprintServiceImp.getByTeam(teamName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.writeValue(outputPath.toFile(), sprints);


    }

    /**
     * Export all developers by team
     */
    @Override
    public void exportDev(Team team) throws IOException {
        Path pathDocuments = Paths.get(System.getProperty("user.home"), "documents");
        Path outputFolder = pathDocuments.resolve("Veloculus-output");
        if (!Files.exists(outputFolder)) {
            Files.createDirectory(outputFolder);
        }

        Path outputPath = outputFolder.resolve("Developers-" + team.getName() + " " + LocalDate.now() + ".json");

        List<Developer> developers = developerServiceImp.getByTeam(team);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.writeValue(outputPath.toFile(), developers);
    }

    /**
     * Export all teams
     */
    @Override
    public void exportTeam() throws IOException {
        Path pathDocuments = Paths.get(System.getProperty("user.home"), "documents");
        Path outputFolder = pathDocuments.resolve("Veloculus-output");
        if (!Files.exists(outputFolder)) {
            Files.createDirectory(outputFolder);
        }

        Path outputPath = outputFolder.resolve("Teams-" + LocalDate.now() + ".json");

        List<Team> teams = teamServiceImp.getAll();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.writeValue(outputPath.toFile(), teams);
    }

    /**
     * Import sprints from a JSON file
     */
    @Override
    public void importSprint(Team team) {
        FileChooser fileChooser = new FileChooser();
        String path = fileChooser.showOpenDialog(null).getAbsolutePath();
        log.info(path);

        Resource resource = new FileSystemResource(path);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try (InputStream inputStream = resource.getInputStream()) {
            List<Sprint> sprints = mapper.readValue(inputStream, new TypeReference<>() {
            });

            for (Sprint sprint : sprints) {
                sprint.setTeam(team);
                sprintServiceImp.save(sprint);
            }

            log.info(sprints.toString());
        } catch (Exception e) {
            log.error("Error in import: " + e.getMessage());
        }


    }

    /**
     * Import developers from a JSON file
     */
    @Override
    public void importDev(Team team) {
        FileChooser fileChooser = new FileChooser();
        String path = fileChooser.showOpenDialog(null).getAbsolutePath();
        log.info(path);

        Resource resource = new FileSystemResource(path);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try (InputStream inputStream = resource.getInputStream()) {
            List<Developer> developers = mapper.readValue(inputStream, new TypeReference<>() {
            });

            for (Developer developer : developers) {
                team.addDeveloper(developer);
                developerServiceImp.save(developer);
                teamServiceImp.save(team);
            }

            log.info(developers.toString());
            log.info(developerServiceImp.getAll().toString());
        } catch (Exception e) {
            log.error("Error in import: " + e.getMessage());
        }

    }

    /**
     * Import teams from a JSON file
     */
    @Override
    public void importTeam() {
        FileChooser fileChooser = new FileChooser();
        String path = fileChooser.showOpenDialog(null).getAbsolutePath();
        log.info(path);

        Resource resource = new FileSystemResource(path);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try (InputStream inputStream = resource.getInputStream()) {
            List<Team> teams = mapper.readValue(inputStream, new TypeReference<>() {
            });

            for (Team team : teams) {
                teamServiceImp.save(team);
            }

            log.info(teams.toString());
        } catch (Exception e) {
            log.error("Error in import: " + e.getMessage());
        }

    }
}
