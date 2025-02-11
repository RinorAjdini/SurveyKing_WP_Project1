package finki.ukim.mk.surveyKing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    private int votes;

    private int orderIndex;

    @Transient
    private double percentage; // Not persisted in the database

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="poll_id")
    private Poll poll;

    @ManyToMany
    @JoinTable(
            name = "user_option", // Name of the join table
            joinColumns = @JoinColumn(name = "option_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
}