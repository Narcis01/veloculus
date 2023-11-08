package com.miele.backend.developer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.miele.backend.availability.Availability;
import com.miele.backend.daysOff.DaysOff;
import com.miele.backend.spikes.Spikes;
import com.miele.backend.team.Team;
import com.miele.backend.topics.Topics;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "developer")
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private int id;

    @Column(name = "name")
    private String name;

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "developer")
    private Set<DaysOff> daysOff = new LinkedHashSet<>();

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "developer")
    private Set<Availability> availability = new LinkedHashSet<>();

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "developer")
    private Set<Topics> topics = new LinkedHashSet<>();

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "developer")
    private Set<Spikes> spikes = new LinkedHashSet<>();

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "team_id")
    private Team team;

    public void addDaysOff(DaysOff days) {
        daysOff.add(days);
        days.setDeveloper(this);
    }

    public void addAvailability(Availability av) {
        availability.add(av);
        av.setDeveloper(this);

    }

    public void addTopics(Topics t) {
        topics.add(t);
        t.setDeveloper(this);
    }

    public void addSpikes(Spikes s) {
        spikes.add(s);
        s.setDeveloper(this);

    }

    @JsonIgnore
    public int getLastSpikes() {
        List<Spikes> spikes = this.getSpikes().stream()
                .sorted(Comparator.comparing(Spikes::getId))
                .toList();
        return spikes.get(spikes.size() - 1).getSpikes();
    }

    @JsonIgnore
    public int getLastAvailability() {
        List<Availability> availabilities = this.getAvailability().stream()
                .sorted(Comparator.comparing(Availability::getId))
                .toList();
        return availabilities.get(availabilities.size() - 1).getAvailabilityPercent();
    }

    @JsonIgnore
    public int getLastDaysOff() {
        List<DaysOff> daysOffs = this.getDaysOff().stream()
                .sorted(Comparator.comparing(DaysOff::getId))
                .toList();
        return daysOffs.get(daysOffs.size() - 1).getDaysOff();
    }

    @Override
    public String toString() {
        return "Developer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", daysOff=" + daysOff.toString() +
                ", availability=" + availability.toString() +
                ", topics=" + topics.toString() +
                ", spikes=" + spikes.toString() +
                '}';
    }
}