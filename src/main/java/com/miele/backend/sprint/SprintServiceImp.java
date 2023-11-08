package com.miele.backend.sprint;

import com.miele.backend.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;


@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class SprintServiceImp implements SprintService {

    private final SprintRepository sprintRepository;

    /**
     * Adding sprint to database
     *
     * @param workingMandays - result from calculator() method
     * @param velocity       - current velocity
     * @param team           - team for current sprint
     */
    @Override
    public Sprint add(double workingMandays, double velocity, Team team) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        Sprint sprint = new Sprint();

        /*
          Velocity
         */
        sprint.setVelocity(velocity);

        /*
          Working mandays
         */
        sprint.setWorkingMandays(workingMandays);

        /*
          Number of developers
         */
        sprint.setDevelopers(team.getDevs().size());

        /*
          Team
         */
        sprint.setTeam(team);
        /*
          Points/Developer/Day
         */
        sprint.setPtsDevDay(Double.parseDouble(numberFormat.format(sprint.getVelocity() / sprint.getWorkingMandays())));


        List<Sprint> sprints = sprintRepository.findAllByTeamName(team.getName());
        /*
          Average PTS/DEV/DAY
         */
        double pts = sprint.getVelocity() / sprint.getWorkingMandays();
        for (Sprint s : sprints) {
            pts += s.getPtsDevDay();
        }
        pts /= (sprints.size() + 1);
        sprint.setAveragePtsDevDay(Double.parseDouble(numberFormat.format(pts)));


        /*
          Average velocity
         */
        double velocityAverage = sprint.getVelocity();
        for (Sprint s : sprints) {
            velocityAverage += s.getVelocity();
        }
        velocityAverage /= (sprints.size() + 1);
        sprint.setAverage((int) velocityAverage);


        /*
          Estimated velocity
         */
        sprint.setEstimatedVelocity((int) Double.parseDouble(numberFormat.format(sprint.getWorkingMandays() / sprint.getAveragePtsDevDay())));


        /*
          Sprint date
         */
        sprint.setDate(LocalDate.now());

        return sprintRepository.save(sprint);
    }

    @Override
    public List<Sprint> getAll() {
        return sprintRepository.findAll();
    }

    @Override
    public List<Sprint> getByTeam(String name) {
        return sprintRepository.findAllByTeamName(name);
    }

    @Override
    public Sprint save(Sprint sprint) {
        return sprintRepository.save(sprint);
    }

    @Override
    public List<Sprint> getByTeamAndOrderByDate(String name) {
        return sprintRepository.findAllByTeamNameOrderByDate(name);
    }

    @Override
    public void removeAll() {
        sprintRepository.deleteAll();
    }

    @Override
    public List<Sprint> getByDate(LocalDate date) {
        return sprintRepository.findAllByDate(date);
    }

    @Override
    public void removeSprint(Sprint sprint) {
        sprintRepository.delete(sprint);
    }

    @Override
    public Sprint getByDateAndVelocity(LocalDate localDate, Double velocity) {
        return sprintRepository.findByDateAndVelocity(localDate,velocity);
    }
}
