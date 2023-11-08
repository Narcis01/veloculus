package com.miele.backend.sprint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miele.backend.team.Team;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "sprint")
public class Sprint {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "velocity")
    private double velocity;

    @Column(name = "average")
    private int average;

    @Column(name = "working_mandays")
    private double workingMandays;

    @Column(name = "estimated_velocity")
    private int estimatedVelocity;

    @Column(name = "pts")
    private double ptsDevDay;

    @Column(name = "average_pts")
    private double averagePtsDevDay;

    @Column(name = "amount_devs")
    private int developers;

    @Column(name = "date")
    private LocalDate date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Override
    public String toString() {
        return "Sprint{" +
                "id=" + id +
                ", velocity=" + velocity +
                ", average=" + average +
                ", workingMandays=" + workingMandays +
                ", estimatedVelocity=" + estimatedVelocity +
                ", ptsDevDay=" + ptsDevDay +
                ", avaragePtsDevDay=" + averagePtsDevDay +
                ", developers=" + developers +
                ", date=" + date +
                ", team=" + team +
                '}';
    }
}