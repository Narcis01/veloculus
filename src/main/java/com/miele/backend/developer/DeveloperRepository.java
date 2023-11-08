package com.miele.backend.developer;


import com.miele.backend.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface DeveloperRepository extends JpaRepository<Developer, Integer> {


    /**
     * Select all developers who have same team
     * @param teamId - current team
     * @return list of developers who have the same team
     */
    List<Developer> findByTeamId(int teamId);
    Developer findByName(String name);
    void deleteByTeam(Team team);
    Developer findByNameAndTeam(String name, Team team);
}