package finki.ukim.mk.surveyKing.service;

import finki.ukim.mk.surveyKing.model.Question;
import finki.ukim.mk.surveyKing.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getQuestionsByPollId(int pollId) {
        return questionRepository.findAll().stream()
                .filter(question -> question.getPoll().getId() == pollId)
                .toList();
    }

    public Question getQuestionById(int id) {
        return questionRepository.findById((long) id).orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(int id) {
        questionRepository.deleteById((long)id);
    }
}