package mutations;
import finki.ukim.mk.surveyKing.model.Option;
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

        mockUser = new User();   //inicijalizirame user ako ni e potreben
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

}

