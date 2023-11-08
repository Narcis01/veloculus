package com.miele.backend.developer;

import com.miele.backend.team.Team;

import java.util.List;

public interface DeveloperService {
    Developer save(Developer developer);
    List<Developer> getAll();
    void delete(String name);
    Developer getByName(String name);
    List<Developer> getByTeam(Team team);
    void removeAll();
    void deleteByTeam(Team team);

    Developer getByNameAndTeam(String name, Team team);

    void deleteByEntity(Developer data);
}
