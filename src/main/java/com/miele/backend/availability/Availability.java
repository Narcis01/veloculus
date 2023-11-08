package com.miele.backend.availability;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miele.backend.developer.Developer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "availability")
public class Availability {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "availability")
    private int availabilityPercent;

    @Column(name = "date")
    private LocalDate date;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Override
    public String toString() {
        return "Availability{" +
                "id=" + id +
                ", availabilityPercent=" + availabilityPercent +
                ", date=" + date +
                '}';
    }


}
