package com.miele.backend.team;

import java.util.List;

public interface TeamService {
    Team save(Team team);
    double calculator(Team team, int days);
    List<Team> getAll();
    void delete(String name);
    Team findByName(String text);
    void removeAll();
}
