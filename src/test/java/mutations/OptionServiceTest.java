package mutations;
import finki.ukim.mk.surveyKing.model.Option;
import finki.ukim.mk.surveyKing.model.Question;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.repository.OptionRepository;
import finki.ukim.mk.surveyKing.service.OptionService;
import finki.ukim.mk.surveyKing.model.Poll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private OptionService optionService;
    private Option mockOption;
    private User mockUser;
    private Poll mockPoll;

    @BeforeEach
    void setUp() {
        mockOption = new Option();
        mockOption.setId(1);
        mockOption.setVotes(0);
        mockOption.setUsers(new ArrayList<>());

        mockUser = new User();
        mockUser = new User();
        mockPoll = new Poll();
        lenient().when(optionRepository.findById(1L)).thenReturn(Optional.of(mockOption));
    }

    @Test
    void testGetOptionById() {
        Option result = optionService.getOptionById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testIncrementVote() {
        optionService.incrementVote(1, mockUser);
        assertEquals(1, mockOption.getVotes());
        assertTrue(mockOption.getUsers().contains(mockUser));
    }

    @Test
    void testDecrementVote() {
        mockOption.setVotes(2);
        mockOption.getUsers().add(mockUser);
        optionService.decrementVote(1, mockUser);
        assertEquals(1, mockOption.getVotes());
        assertFalse(mockOption.getUsers().contains(mockUser));
    }

    @Test
    void testCreateOption() {
        when(optionRepository.save(any(Option.class))).thenReturn(mockOption);
        Option result = optionService.createOption(mockOption);
        assertNotNull(result);
        verify(optionRepository, times(1)).save(mockOption);
    }

    @Test
    void testGetUsersWhoSelectedOption() {
        List<User> users = List.of(mockUser);
        mockOption.setUsers(users);
        List<User> result = optionService.getUsersWhoSelectedOption(1);
        assertEquals(1, result.size());
        assertEquals(mockUser, result.get(0));
    }

    @Test
    void testGetSelectedOptionsByPollAndUser() {
        when(optionRepository.findSelectedOptionsByPollAndUser(mockPoll, mockUser)).thenReturn(List.of(mockOption));
        List<Option> result = optionService.getSelectedOptionsByPollAndUser(mockPoll, mockUser);
        assertEquals(1, result.size());
        assertEquals(mockOption, result.get(0));
    }

    @Test
    void testDeleteSelectedOptionsByPollAndUser() {
        List<Option> selectedOptions = new ArrayList<>(List.of(mockOption));
        mockOption.getUsers().add(mockUser);
        when(optionRepository.findSelectedOptionsByPollAndUser(mockPoll, mockUser)).thenReturn(selectedOptions);
        optionService.deleteSelectedOptionsByPollAndUser(mockPoll, mockUser);
        assertFalse(mockOption.getUsers().contains(mockUser));
        verify(optionRepository, times(1)).save(mockOption);
    }

    @Test
    void testDeleteOption() {
        optionService.deleteOption(1);
        verify(optionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetOptionsByQuestionId() {
        int questionId = 1;
        Option option1 = new Option();
        option1.setId(1);
        Question question1 = new Question();
        question1.setId(questionId);
        option1.setQuestion(question1);

        Option option2 = new Option();
        option2.setId(2);
        Question question2 = new Question();
        question2.setId(2);
        option2.setQuestion(question2);

        List<Option> allOptions = List.of(option1, option2);
        when(optionRepository.findAll()).thenReturn(allOptions);

        List<Option> result = optionService.getOptionsByQuestionId(questionId);

        assertEquals(1, result.size(), "Only one option should match the question ID");
        assertEquals(option1, result.get(0), "The returned option should match the question ID");

        assertFalse(result.contains(option2), "Options with a different question ID should not be included");
    }
    @Test
    void testGetOptionsByQuestionId_NoMatchingOptions() {
        int questionId = 1;
        Option option = new Option();
        option.setId(1);
        Question question = new Question();
        question.setId(2);
        option.setQuestion(question);

        List<Option> allOptions = List.of(option);
        when(optionRepository.findAll()).thenReturn(allOptions);

        List<Option> result = optionService.getOptionsByQuestionId(questionId);

        assertTrue(result.isEmpty(), "The result should be empty when no options match the question ID");
    }

    @Test
    void testGetOptionById_OptionNotFound() {
        int optionId = 99;
        when(optionRepository.findById((long) optionId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            optionService.getOptionById(optionId);
        });

        assertEquals("Option not found", exception.getMessage(), "The exception message should match");
    }

    @Test
    void testGetUsersWhoSelectedOption_InvalidOptionId() {
        int optionId = 99;
        when(optionRepository.findById((long) optionId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            optionService.getUsersWhoSelectedOption(optionId);
        });

        assertEquals("Invalid option ID", exception.getMessage(), "The exception message should match");
    }
    @Test
    void testDecrementVote_InvalidOptionId() {
        when(optionRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            optionService.decrementVote(99, mockUser);
        });

        assertEquals("Invalid option ID", exception.getMessage());
    }
    @Test
    void testDecrementVote_VotesCannotGoBelowZero() {
        mockOption.setVotes(0);

        optionService.decrementVote(1, mockUser);

        assertEquals(0, mockOption.getVotes(), "Votes should not go below zero");
    }
    @Test
    void testIncrementVote_InvalidOptionId() {
        when(optionRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            optionService.incrementVote(99, mockUser);
        });

        assertEquals("Invalid option ID", exception.getMessage());
    }


}

