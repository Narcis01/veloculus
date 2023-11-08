package com.miele.backend.developer;

import com.miele.backend.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class DeveloperServiceImp implements DeveloperService{

    private final DeveloperRepository developerRepository;

    @Override
    public Developer save(Developer developer) {
        return developerRepository.save(developer);
    }

    @Override
    public List<Developer> getAll() {
        return developerRepository.findAll();
    }

    @Override
    public void delete(String name) {
        Developer developer = developerRepository.findByName(name);
        developerRepository.deleteById(developer.getId());

    }

    @Override
    public Developer getByName(String name) {
        return developerRepository.findByName(name);

    }

    @Override
    public List<Developer> getByTeam(Team team) {
        return developerRepository.findByTeamId(team.getId());
    }

    @Override
    public void removeAll() {
        developerRepository.deleteAll();
    }

    @Override
    public void deleteByTeam(Team team) {
        developerRepository.deleteByTeam(team);
    }

    @Override
    public Developer getByNameAndTeam(String name, Team team) {
        return developerRepository.findByNameAndTeam(name,team);
    }

    @Override
    public void deleteByEntity(Developer data) {
        developerRepository.delete(data);
    }
}
