package com.miele.veloculus;


import com.miele.backend.availability.Availability;
import com.miele.backend.daysOff.DaysOff;
import com.miele.backend.developer.Developer;
import com.miele.backend.spikes.Spikes;
import com.miele.backend.sprint.Sprint;
import com.miele.backend.sprint.SprintRepository;
import com.miele.backend.sprint.SprintServiceImp;
import com.miele.backend.team.Team;
import com.miele.backend.topics.Topics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class SprintServiceTest {

    @Mock
    SprintRepository sprintRepository;

    @InjectMocks
    SprintServiceImp sprintServiceImp;

    @Test
    public void addSprintTest() {
        double workingMandays = 10;
        double velocity = 10;
        /*
        Creating sets for every entity to test
         */
        Availability availability = Availability.builder().availabilityPercent(100).build();
        Set<Availability> availabilitySet = new HashSet<>();
        availabilitySet.add(availability);

        DaysOff daysOff = DaysOff.builder().daysOff(0).build();
        Set<DaysOff> daysOffSet = new HashSet<>();
        daysOffSet.add(daysOff);

        Spikes spikes = Spikes.builder().spikes(0).build();
        Set<Spikes> spikesSet = new HashSet<>();
        spikesSet.add(spikes);

        Topics topics = Topics.builder().topics("dev").build();
        Set<Topics> topicsSet = new HashSet<>();
        topicsSet.add(topics);

        Developer developer = Developer.builder().name("NARCIS")
                .availability(availabilitySet)
                .daysOff(daysOffSet)
                .spikes(spikesSet)
                .topics(topicsSet).build();
        Set<Developer> developerSet = new HashSet<>();
        developerSet.add(developer);

        Team team = Team.builder().name("MOVE").devs(developerSet).id(1).build();

        Sprint sprint = Sprint.builder().team(team)
                .workingMandays(10).velocity(1).estimatedVelocity(1)
                .developers(1).average(1).date(LocalDate.now())
                .ptsDevDay(1).averagePtsDevDay(1).build();
        List<Sprint> sprintList = new ArrayList<>();
        sprintList.add(sprint);

        /*
            Actual test
         */


        Mockito.when(sprintRepository.findAllByTeamName(team.getName())).thenReturn(sprintList);

        Sprint expected = Sprint.builder().id(1).velocity(10).team(team).workingMandays(10)
                .average(5).estimatedVelocity(20).ptsDevDay(1).averagePtsDevDay(0.5)
                .developers(1).build();

        Mockito.when(sprintRepository.save(any())).thenReturn(expected);
        Sprint result = sprintServiceImp.add(workingMandays, velocity, team);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getByDateAndVelocityTest(){
        LocalDate localDate = LocalDate.now();
        Double velocity = 10.0;
        Sprint expected = Sprint.builder().velocity(velocity).date(localDate).build();
        Mockito.when(sprintRepository.findByDateAndVelocity(any(),any())).thenReturn(expected);

        Sprint result = sprintServiceImp.getByDateAndVelocity(localDate,velocity);

        Assert.assertEquals(result,expected);
    }

    @Test
    public void getByTeamAndOrderByDateTest(){
        LocalDate localDate1 = LocalDate.of(2023,10,2);
        LocalDate localDate2 = LocalDate.of(2024,10,2);
        LocalDate localDate3 = LocalDate.of(2021,10,2);

        Team team = Team.builder().name("MOVE").build();
        Sprint sprint1 = Sprint.builder().date(localDate1).team(team).build();
        Sprint sprint2 = Sprint.builder().date(localDate2).team(team).build();
        Sprint sprint3 = Sprint.builder().date(localDate3).team(team).build();

        List<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint3);
        sprints.add(sprint1);
        sprints.add(sprint2);

        Mockito.when(sprintRepository.findAllByTeamNameOrderByDate("MOVE")).thenReturn(sprints);

        List<Sprint> result = sprintServiceImp.getByTeamAndOrderByDate("MOVE");

        Assert.assertEquals(sprints,result);
    }
}
