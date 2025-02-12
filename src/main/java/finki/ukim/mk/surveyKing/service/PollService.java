package finki.ukim.mk.surveyKing.service;

import finki.ukim.mk.surveyKing.model.*;
import finki.ukim.mk.surveyKing.repository.PollRepository;
import finki.ukim.mk.surveyKing.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PollService {
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;
    public List<Poll> getAllPols(){
        return pollRepository.findAll();
    }
    public Poll getPollById(int id){
        return pollRepository.findById(id).orElseThrow();
    }
    public void createPoll(Poll poll){
     pollRepository.save(poll);
    }
    public void deletePoll(int id) {
        pollRepository.deleteById(id);
    }

    @Transactional
    public void updatePoll(Poll poll, PollData pollData) {
        poll.setName(pollData.getName());

        List<Question> existingQuestions = poll.getQuestions();
        List<QuestionData> newQuestionsData = pollData.getQuestions();

        for (int i = 0; i < newQuestionsData.size(); i++) {
            QuestionData questionData = newQuestionsData.get(i);
            Question question;

            if (i < existingQuestions.size()) {
                question = existingQuestions.get(i);
                question.setText(questionData.getText());
            } else {
                question = new Question();
                question.setText(questionData.getText());
                question.setPoll(poll);
                poll.getQuestions().add(question);
            }

            List<Option> existingOptions = question.getOptions();
            List<OptionData> newOptionsData = questionData.getOptions();

            for (int j = 0; j < newOptionsData.size(); j++) {
                OptionData optionData = newOptionsData.get(j);
                Option option;

                if (j < existingOptions.size()) {
                    option = existingOptions.get(j);
                    option.setDescription(optionData.getDescription());
                } else {
                    option = new Option();
                    option.setDescription(optionData.getDescription());
                    option.setQuestion(question);
                    option.setPoll(poll);
                    question.getOptions().add(option);
                }
            }
            pollRepository.save(poll);
        }
    }

    public void likePoll(int pollId, Long userId) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new RuntimeException("Poll not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!poll.getLikedUsers().contains(user)) {
            poll.getLikedUsers().add(user);
        }
        else{
            poll.getLikedUsers().remove(user);
        }
        pollRepository.save(poll);
    }
    public List<Poll> searchPollsByName(String query) {
        return pollRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Poll> getPollsByUser(User user) {
        return pollRepository.findByUser(user);
    }

    public List<Poll> getAllPollsSortedByLikesAsc() {
        return pollRepository.findAllByOrderByLikesAsc();
    }

    public List<Poll> getAllPollsSortedByLikesDesc() {
        return pollRepository.findAllByOrderByLikesDesc();
    }

    public List<Poll> getAllPollsSortedByCreatedAtAsc() {
        return pollRepository.findAllByOrderByCreatedAtAsc();
    }

    public List<Poll> getAllPollsSortedByCreatedAtDesc() {
        return pollRepository.findAllByOrderByCreatedAtDesc();
    }
    public int getParticipantCount(int pollId) {
        return pollRepository.findDistinctParticipantsCountByPollId(pollId);
    }
}
