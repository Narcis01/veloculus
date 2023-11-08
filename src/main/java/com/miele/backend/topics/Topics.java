package com.miele.backend.topics;


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
@Table(name = "topics")
public class Topics {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "topics")
    String topics;

    @Column(name = "date")
    private LocalDate date;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Override
    public String toString() {
        return "Topics{" +
                "id=" + id +
                ", topics='" + topics + '\'' +
                ", date=" + date +
                '}';
    }
}
