package mutations;

import finki.ukim.mk.surveyKing.model.Poll;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.service.PollSecurityService;
import finki.ukim.mk.surveyKing.service.PollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PollSecurityServiceTest {

    @Mock
    private PollService pollService;

    @InjectMocks
    private PollSecurityService pollSecurityService;
    private Poll mockPoll;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("user123");

        mockPoll = new Poll();
        mockPoll.setId(1);
        mockPoll.setUser(mockUser);

        lenient().when(pollService.getPollById(1)).thenReturn(mockPoll);
    }

    @Test
    void testIsPollOwner_WhenUserIsOwner() {
        boolean result = pollSecurityService.isPollOwner("user123", 1);
        assertTrue(result);
    }

    @Test
    void testIsPollOwner_WhenUserIsNotOwner() {
        boolean result = pollSecurityService.isPollOwner("notTheOwner", 1);
        assertFalse(result);
    }

    @Test
    void testIsPollOwner_WhenPollDoesNotExist() {
        when(pollService.getPollById(2)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            pollSecurityService.isPollOwner("user123", 2);
        });
    }
}