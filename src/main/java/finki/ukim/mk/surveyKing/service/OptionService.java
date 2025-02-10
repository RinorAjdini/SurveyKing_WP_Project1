package finki.ukim.mk.surveyKing.service;

import finki.ukim.mk.surveyKing.model.Option;
import finki.ukim.mk.surveyKing.model.Poll;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {
    @Autowired
    private OptionRepository optionRepository;

    public List<Option> getOptionsByQuestionId(int questionId) {
        return optionRepository.findAll().stream()
                .filter(option -> option.getQuestion().getId() == questionId)
                .toList();
    }

    public Option getOptionById(int id) {
        return optionRepository.findById((long)id).orElseThrow(() -> new RuntimeException("Option not found"));
    }
    public List<User> getUsersWhoSelectedOption(int optionId) {
        Option option = optionRepository.findById((long) optionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid option ID"));
        return option.getUsers(); // Returns the list of users who selected this option
    }

    // Increment votes and associate the user with the option
    public void incrementVote(int optionId, User user) {
        Option option = optionRepository.findById((long) optionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid option ID"));
        option.setVotes(option.getVotes() + 1);
        // Add the user to the set if not already present
        if (!option.getUsers().contains(user)) {
            option.getUsers().add(user);
        }
        optionRepository.save(option);
    }
    public void decrementVote(int optionId, User user) {
        Option option = optionRepository.findById((long) optionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid option ID"));
        if (option.getVotes() > 0) {
            option.setVotes(option.getVotes() - 1);
        }
        option.getUsers().remove(user);
        optionRepository.save(option);
    }
    public Option createOption(Option option) {
        return optionRepository.save(option);
    }

    public List<Option> getSelectedOptionsByPollAndUser(Poll poll, User user) {
        return optionRepository.findSelectedOptionsByPollAndUser(poll, user);
    }
    public void deleteSelectedOptionsByPollAndUser(Poll poll, User user) {
        List<Option> selectedOptions = optionRepository.findSelectedOptionsByPollAndUser(poll, user);
        for (Option option : selectedOptions) {
            option.getUsers().remove(user);
            optionRepository.save(option);
        }
    }
    public void deleteOption(int id) {
        optionRepository.deleteById((long)id);
    }
}
