package finki.ukim.mk.surveyKing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text; // Text of the question
    private int orderIndex;
    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll; // Reference to the Poll

    @Transient
    private int totalVotes;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
}