package com.miele.veloculus;


import com.miele.backend.availability.Availability;
import com.miele.backend.daysOff.DaysOff;
import com.miele.backend.developer.Developer;
import com.miele.backend.developer.DeveloperRepository;
import com.miele.backend.spikes.Spikes;
import com.miele.backend.team.Team;
import com.miele.backend.team.TeamRepository;
import com.miele.backend.team.TeamServiceImp;
import com.miele.backend.topics.Topics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;
    @Mock
    DeveloperRepository developerRepository;

    @InjectMocks
    TeamServiceImp teamServiceImp;

    @Test
    public void SaveTeamTest() {
        Team team = Team.builder().name("MOVE").build();

        Mockito.when(teamRepository.save(team)).thenReturn(team);
        Team result = teamServiceImp.save(team);

        Assert.assertEquals(team, result);
    }

    @Test
    public void FindByNameTest() {
        Team expected = Team.builder().name("MOVE").build();

        Mockito.when(teamRepository.findByName(expected.getName())).thenReturn(expected);
        Team result = teamServiceImp.findByName(expected.getName());

        Assert.assertEquals(expected, result);
    }

    @Test
    public void CalculatorTestForPerfectStats() {
        /*
        Creating sets for every entity for developer and making a set of developers for test
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

        Developer developer = Developer.builder().name("Narcis")
                .availability(availabilitySet)
                .daysOff(daysOffSet)
                .spikes(spikesSet)
                .topics(topicsSet).build();
        Set<Developer> developerSet = new HashSet<>();
        developerSet.add(developer);
        Team team = Team.builder().name("MOVE").devs(developerSet).id(1).build();

        List<Developer> developerList = new ArrayList<>();
        developerList.add(developer);
        Mockito.when(developerRepository.findByTeamId(1)).thenReturn(developerList);

        double result = teamServiceImp.calculator(team,10);
        assertEquals(10.0,result);
    }
    @Test
    public void CalculatorTestWithMultipleDevs() {
        /*
        Creating sets for every entity for developer and making a set of developers for test
         */
        Availability availability1 = Availability.builder().availabilityPercent(100).id(1).build();
        Set<Availability> availabilitySet1 = new HashSet<>();
        availabilitySet1.add(availability1);

        DaysOff daysOff1 = DaysOff.builder().daysOff(0).id(1).build();
        Set<DaysOff> daysOffSet1 = new HashSet<>();
        daysOffSet1.add(daysOff1);

        Spikes spikes1 = Spikes.builder().spikes(0).id(1).build();
        Set<Spikes> spikesSet1 = new HashSet<>();
        spikesSet1.add(spikes1);

        Topics topics1 = Topics.builder().topics("dev").id(1).build();
        Set<Topics> topicsSet1 = new HashSet<>();
        topicsSet1.add(topics1);

        Developer developer1 = Developer.builder().name("Narcis").id(1)
                .availability(availabilitySet1)
                .daysOff(daysOffSet1)
                .spikes(spikesSet1)
                .topics(topicsSet1).build();
        /*
        Developer 2
         */
        Availability availability2 = Availability.builder().availabilityPercent(90).id(2).build();
        Set<Availability> availabilitySet2 = new HashSet<>();
        availabilitySet2.add(availability2);

        DaysOff daysOff2 = DaysOff.builder().daysOff(2).id(2).build();
        Set<DaysOff> daysOffSet2 = new HashSet<>();
        daysOffSet2.add(daysOff2);

        Spikes spikes2 = Spikes.builder().spikes(0).id(2).build();
        Set<Spikes> spikesSet2 = new HashSet<>();
        spikesSet2.add(spikes2);

        Topics topics2 = Topics.builder().topics("dev").id(2).build();
        Set<Topics> topicsSet2 = new HashSet<>();
        topicsSet2.add(topics2);

        Developer developer2 = Developer.builder().name("Mihai").id(2)
                .availability(availabilitySet2)
                .daysOff(daysOffSet2)
                .spikes(spikesSet2)
                .topics(topicsSet2).build();

        Set<Developer> developerSet = new HashSet<>();
        developerSet.add(developer1);
        developerSet.add(developer2);

        Team team = Team.builder().name("MOVE").devs(developerSet).id(1).build();

        List<Developer> developerList = new ArrayList<>();
        developerList.add(developer1);
        developerList.add(developer2);
        /*
            Actual test
         */
        Mockito.when(developerRepository.findByTeamId(1)).thenReturn(developerList);

        double result = teamServiceImp.calculator(team,10);
        assertEquals(17.0,result);
    }
}
