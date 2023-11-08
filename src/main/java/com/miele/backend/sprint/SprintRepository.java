package com.miele.backend.sprint;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Integer> {
    /**
     * Return all sprints that have the same team
     * @param name name of the team
     * @return sprints that have the same team
     */
    List<Sprint> findAllByTeamName(String name);
    List<Sprint> findAllByTeamNameOrderByDate(String name);
    Sprint findByDate(LocalDate date);
    List<Sprint> findAllByDate(LocalDate date);
    Sprint findByDateAndVelocity(LocalDate date, Double velocity);
}