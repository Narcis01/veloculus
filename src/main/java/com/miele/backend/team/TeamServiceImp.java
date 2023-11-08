package com.miele.backend.team;

import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class TeamServiceImp implements TeamService {

    private final TeamRepository teamRepository;
    private final DeveloperRepository developerRepository;

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);

    }

    /**
     * Calculator for working mandays
     *
     * @param team - team we are calculating
     * @param days - number of days in sprint
     * @return - the result for working mandays
     */
    @Override
    public double calculator(Team team, int days) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        List<Developer> developers = developerRepository.findByTeamId(team.getId());
        double result = 0;
        double spikeDays = team.getDevs().stream().mapToInt(Developer::getLastSpikes).sum();
        spikeDays = Double.parseDouble(numberFormat.format(spikeDays / 8));

        for (Developer dev : developers) {
            result += (days - dev.getLastDaysOff()) * dev.getLastAvailability() / 100;
        }
        return Double.parseDouble(numberFormat.format(result - spikeDays));

    }

    @Override
    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    @Override
    public void delete(String name) {
        Team team = teamRepository.findByName(name);
        teamRepository.deleteById(team.getId());
    }

    @Override
    public Team findByName(String text) {
        return teamRepository.findByName(text);
    }

    @Override
    public void removeAll() {
        teamRepository.deleteAll();
    }
}
