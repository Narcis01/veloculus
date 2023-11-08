package com.miele.backend.sprint;

import com.miele.backend.team.Team;

import java.time.LocalDate;
import java.util.List;

public interface SprintService {

    Sprint add(double workingMandays, double velocity, Team team);

    List<Sprint> getAll();

    List<Sprint> getByTeam(String name);

    Sprint save(Sprint sprint);

    List<Sprint> getByTeamAndOrderByDate(String name);

    void removeAll();

    List<Sprint> getByDate(LocalDate date);

    void removeSprint(Sprint sprint);

    Sprint getByDateAndVelocity(LocalDate localDate, Double velocity);
}
