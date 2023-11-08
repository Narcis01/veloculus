package com.miele.backend.spikes;

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
@Table(name = "spikes")
public class Spikes{
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "spikes")
    private int spikes;

    @Column(name = "data")
    private LocalDate date;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Override
    public String toString() {
        return "Spikes{" +
                "id=" + id +
                ", spikes=" + spikes +
                ", date=" + date +
                '}';
    }


}
