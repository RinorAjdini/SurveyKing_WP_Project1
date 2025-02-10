package finki.ukim.mk.surveyKing.model;

import java.util.ArrayList;
import java.util.List;

public class QuestionData {
    private String text;
    private List<OptionData> options = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public void setOptions(List<OptionData> options) {
        this.options = options;
    }
}
