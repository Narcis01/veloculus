package com.miele.backend.team;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miele.backend.developer.Developer;
import com.miele.backend.sprint.Sprint;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "team")
    private String name;

    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team",
            cascade = CascadeType.REMOVE)
    Set<Developer> devs = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Sprint> sprints = new HashSet<>();

    public void addDeveloper(Developer dev) {
        devs.add(dev);
        dev.setTeam(this);
    }

    @Override
    public String toString() {
        return name;
    }
}