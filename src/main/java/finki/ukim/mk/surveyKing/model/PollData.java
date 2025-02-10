package finki.ukim.mk.surveyKing.model;

import java.util.ArrayList;
import java.util.List;

public class PollData {
    private String name;
    private List<QuestionData> questions = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionData> questions) {
        this.questions = questions;
    }
}
