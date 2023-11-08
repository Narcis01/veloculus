package com.miele.backend.daysOff;

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
@Table(name = "daysOff")
public class DaysOff {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "days_off")
    private int daysOff;

    @Column(name = "data")
    private LocalDate date;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Override
    public String toString() {
        return "DaysOff{" +
                "id=" + id +
                ", daysOff=" + daysOff +
                ", date=" + date +
                '}';
    }

}
