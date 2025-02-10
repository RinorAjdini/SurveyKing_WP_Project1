package finki.ukim.mk.surveyKing.service;

import finki.ukim.mk.surveyKing.model.Poll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PollSecurityService {

    @Autowired
    private PollService pollService;

    public boolean isPollOwner(String username, int pollId) {
        Poll poll = pollService.getPollById(pollId);
        return poll.getUser().getUsername().equals(username);
    }
}