package mutations;
import finki.ukim.mk.surveyKing.model.*;
import finki.ukim.mk.surveyKing.model.QuestionData;
import finki.ukim.mk.surveyKing.model.OptionData;
import finki.ukim.mk.surveyKing.repository.PollRepository;
import finki.ukim.mk.surveyKing.repository.UserRepository;
import finki.ukim.mk.surveyKing.service.PollService;
import finki.ukim.mk.surveyKing.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PollService pollService;

    @InjectMocks
    private UserService userService;


    private Poll poll;
    private User user;

    @BeforeEach
    void setUp() {
        poll = new Poll();
        poll.setId(1);
        poll.setName("Test Poll");

        // Initialize User object
        user = new User();
        user.setId(1L); // Make sure user has an ID

        // Mock Question and Option
        Question question = new Question();
        question.setText("What is your favorite color?");
        question.setPoll(poll);

        Option option = new Option();
        option.setDescription("Red");
        option.setQuestion(question);
        question.getOptions().add(option);

        poll.getQuestions().add(question);

        // Create mock PollData with updated data
        QuestionData questionData = new QuestionData();
        questionData.setText("What is your favorite fruit?");

        OptionData optionData = new OptionData();
        optionData.setDescription("Apple");

        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        // Mock PollRepository save
        lenient().when(pollRepository.save(any(Poll.class))).thenReturn(poll);
    }



    @Test
    void testGetAllPols() {
        List<Poll> polls = List.of(poll);
        when(pollRepository.findAll()).thenReturn(polls);

        List<Poll> result = pollService.getAllPols();
        assertEquals(1, result.size());
        verify(pollRepository, times(1)).findAll();
    }
    @Test
    void testUpdatePoll() {
        // Create QuestionData and OptionData
        QuestionData questionData = new QuestionData();
        questionData.setText("What is your favorite fruit?");

        OptionData optionData = new OptionData();
        optionData.setDescription("Apple");

        questionData.setOptions(List.of(optionData));

        // Create PollData with the updated information
        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        // Call the updatePoll method
        pollService.updatePoll(poll, pollData);

        // Verify that the poll's name was updated
        assertEquals("Updated Poll", poll.getName());

        // Verify that the question text was updated
        assertEquals("What is your favorite fruit?", poll.getQuestions().get(0).getText());

        // Verify that the option description was updated
        assertEquals("Apple", poll.getQuestions().get(0).getOptions().get(0).getDescription());

        // Verify that save was called on the repository
        verify(pollRepository, times(1)).save(poll);
    }

    @Test
    void testGetPollById_Success() {
        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));

        Poll result = pollService.getPollById(1);
        assertNotNull(result);
        assertEquals("Test Poll", result.getName());
    }

    @Test
    void testGetPollById_NotFound() {
        when(pollRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> pollService.getPollById(99));
    }

    @Test
    void testCreatePoll() {
        when(pollRepository.save(any(Poll.class))).thenReturn(poll);

        pollService.createPoll(poll);
        verify(pollRepository, times(1)).save(poll);
    }

    @Test
    void testDeletePoll() {
        doNothing().when(pollRepository).deleteById(1);

        pollService.deletePoll(1);
        verify(pollRepository, times(1)).deleteById(1);
    }

    @Test
    void testLikePoll() {
        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        pollService.likePoll(1, 1L);
        assertTrue(poll.getLikedUsers().contains(user));

        pollService.likePoll(1, 1L);
        assertFalse(poll.getLikedUsers().contains(user));
    }

    @Test
    void testGetLikes() {
        User user1 = new User();
        user1.setId(2L);
        User user2 = new User();
        user2.setId(3L);

        poll.getLikedUsers().add(user1);
        poll.getLikedUsers().add(user2);

        assertEquals(2, poll.getLikes());
    }

    @Test
    void testGetAllPollsSortedByLikesAsc() {
        when(pollRepository.findAllByOrderByLikesAsc()).thenReturn(List.of(poll));
        List<Poll> result = pollService.getAllPollsSortedByLikesAsc();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllPollsSortedByLikesDesc() {
        when(pollRepository.findAllByOrderByLikesDesc()).thenReturn(List.of(poll));
        List<Poll> result = pollService.getAllPollsSortedByLikesDesc();
        assertEquals(1, result.size());
    }
    @Test
    void testFindPollsByNameContainingIgnoreCase() {
        String query = "Test Poll"; // Example query
        List<Poll> polls = List.of(poll); // Assume `poll` is an object created in the `setUp()` method

        // Mock the behavior of the pollRepository
        when(pollRepository.findByNameContainingIgnoreCase(query)).thenReturn(polls);

        // Call the method in the service layer
        List<Poll> result = pollService.searchPollsByName(query);

        // Verify the result
        assertEquals(1, result.size()); // Verify that the returned list has 1 poll
        assertEquals("Test Poll", result.get(0).getName()); // Verify that the poll name matches the query

        // Verify that the repository method was called with the correct argument
        verify(pollRepository, times(1)).findByNameContainingIgnoreCase(query);
    }
    @Test
    void testFindPollsByUser() {
        User user = new User(); // Mock the user, make sure to set the user ID if needed
        user.setId(1L); // Set user ID to 1 (you can change this as necessary)

        List<Poll> polls = List.of(poll); // Assume `poll` is an object created in the `setUp()` method

        // Mock the behavior of the pollRepository
        when(pollRepository.findByUser(user)).thenReturn(polls);

        // Call the method in the service layer
        List<Poll> result = pollService.getPollsByUser(user);

        // Verify the result
        assertEquals(1, result.size()); // Verify that the returned list has 1 poll
        assertEquals("Test Poll", result.get(0).getName()); // Verify that the poll name matches the mock poll

        // Verify that the repository method was called with the correct argument
        verify(pollRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetAllPollsSortedByCreatedAtAsc() {
        when(pollRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(poll));
        List<Poll> result = pollService.getAllPollsSortedByCreatedAtAsc();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllPollsSortedByCreatedAtDesc() {
        when(pollRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(poll));
        List<Poll> result = pollService.getAllPollsSortedByCreatedAtDesc();
        assertEquals(1, result.size());
    }
    @Test
    void testCreatePollWithNoQuestions() {
        Poll emptyPoll = new Poll();
        emptyPoll.setId(2);
        emptyPoll.setName("Empty Poll");

        when(pollRepository.save(any(Poll.class))).thenReturn(emptyPoll);

        pollService.createPoll(emptyPoll);
        verify(pollRepository, times(1)).save(emptyPoll);

        assertEquals("Empty Poll", emptyPoll.getName());
        assertTrue(emptyPoll.getQuestions().isEmpty());
    }

    @Test
    void testUpdatePollWithMultipleQuestions() {
        QuestionData questionData1 = new QuestionData();
        questionData1.setText("What is your favorite color?");
        OptionData optionData1 = new OptionData();
        optionData1.setDescription("Red");
        questionData1.setOptions(List.of(optionData1));

        QuestionData questionData2 = new QuestionData();
        questionData2.setText("What is your favorite fruit?");
        OptionData optionData2 = new OptionData();
        optionData2.setDescription("Apple");
        questionData2.setOptions(List.of(optionData2));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll with Multiple Questions");
        pollData.setQuestions(List.of(questionData1, questionData2));

        pollService.updatePoll(poll, pollData);

        assertEquals("Updated Poll with Multiple Questions", poll.getName());
        assertEquals(2, poll.getQuestions().size()); // Ensure two questions are added
    }

    @Test
    void testFindPollsByNameContainingIgnoreCase_NoResults() {
        String query = "NonExistentPoll";
        when(pollRepository.findByNameContainingIgnoreCase(query)).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.searchPollsByName(query);

        assertTrue(result.isEmpty()); // No polls should be returned
        verify(pollRepository, times(1)).findByNameContainingIgnoreCase(query);
    }
    @Test
    void testDeletePollNotFound() {
        doThrow(new NoSuchElementException("Poll not found")).when(pollRepository).deleteById(99);

        assertThrows(NoSuchElementException.class, () -> pollService.deletePoll(99));
    }




    @Test
    void testAddOptionToQuestion() {
        Question question = new Question();
        question.setText("What is your favorite color?");
        poll.getQuestions().add(question);

        Option option = new Option();
        option.setDescription("Blue");
        option.setQuestion(question);
        option.setPoll(poll);

        question.getOptions().add(option);

        assertEquals(1, question.getOptions().size()); // Only one option should be added
        assertEquals("Blue", question.getOptions().get(0).getDescription());
    }
    @Test
    void testLikePollByMultipleUsers() {
        User user2 = new User();
        user2.setId(2L);

        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        pollService.likePoll(1, 1L); // User 1 likes the poll
        pollService.likePoll(1, 2L); // User 2 likes the poll

        assertTrue(poll.getLikedUsers().contains(user));
        assertTrue(poll.getLikedUsers().contains(user2));
        assertEquals(2, poll.getLikedUsers().size()); // Two users should have liked the poll
    }

    @Test
    void testLikePollWhenAlreadyLiked() {
        Integer pollId = 1; // Example poll ID of type Integer
        User user = new User(); // Use a valid user object

        // Simulate that the poll doesn't exist in the repository (i.e., poll is not found)
        when(pollRepository.findById(pollId)).thenReturn(Optional.empty()); // Mocking poll not found

        assertThrows(RuntimeException.class, () -> pollService.likePoll(pollId, user.getId()));
    }

    @Test
    void testUpdatePoll_RemovesExtraQuestionsAndOptions() {
        // Setup existing questions and options
        Question existingQuestion1 = new Question();
        existingQuestion1.setText("Old Question 1");
        existingQuestion1.setPoll(poll);

        Question existingQuestion2 = new Question();
        existingQuestion2.setText("Old Question 2"); // This one should be removed
        existingQuestion2.setPoll(poll);

        poll.getQuestions().add(existingQuestion1);
        poll.getQuestions().add(existingQuestion2);

        Option existingOption1 = new Option();
        existingOption1.setDescription("Old Option 1");
        existingOption1.setQuestion(existingQuestion1);

        Option existingOption2 = new Option();
        existingOption2.setDescription("Old Option 2"); // This one should be removed
        existingOption2.setQuestion(existingQuestion1);

        existingQuestion1.getOptions().add(existingOption1);
        existingQuestion1.getOptions().add(existingOption2);

        // Mock updated PollData with fewer questions and options
        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");

        OptionData optionData = new OptionData();
        optionData.setDescription("New Option");

        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        // Call updatePoll
        pollService.updatePoll(poll, pollData);

        // Assertions
        assertEquals(1, poll.getQuestions().size()); // One question remains
        assertEquals("New Question", poll.getQuestions().get(0).getText());

        assertEquals(1, poll.getQuestions().get(0).getOptions().size()); // One option remains
        assertEquals("New Option", poll.getQuestions().get(0).getOptions().get(0).getDescription());
    }
    @Test
    void shouldRemoveExtraOptionsWhenNewOptionsAreFewer() {
        Option option1 = new Option();
        option1.setDescription("A");
        Option option2 = new Option();
        option2.setDescription("B");
        Option option3 = new Option();
        option3.setDescription("C");

        List<Option> existingOptions = new ArrayList<>(List.of(option1, option2, option3));
        List<Option> newOptionsData = new ArrayList<>(List.of(option1, option2)); // Fewer but same options

        if (existingOptions.size() > newOptionsData.size()) {
            existingOptions.subList(newOptionsData.size(), existingOptions.size()).clear();
        }

        assertEquals(2, existingOptions.size()); // Ensure extra option is removed
        assertEquals("A", existingOptions.get(0).getDescription());
        assertEquals("B", existingOptions.get(1).getDescription());
    }


    @Test
    void shouldReturnNullWhenUserNotFound() {
        Long nonExistentUserId = 999L;
        when(userRepository.findByUsername(String.valueOf(nonExistentUserId))).thenReturn(Optional.empty());

        User result = userService.findByUsername(String.valueOf(nonExistentUserId));

        assertNull(result); // Since the method returns null, we check for null instead of an exception.
    }
    @Test
    void testUpdatePoll_SetsName() {
        PollData pollData = new PollData();
        pollData.setName("Updated Poll");

        pollService.updatePoll(poll, pollData);
        assertEquals("Updated Poll", poll.getName()); // Verify name is set
    }

    @Test
    void testLikePoll_AddsUser() {
        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        pollService.likePoll(1, 1L);
        assertTrue(poll.getLikedUsers().contains(user)); // Verify user is added
    }

    @Test
    void testLikePoll_RemovesUser() {
        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        pollService.likePoll(1, 1L); // Like
        pollService.likePoll(1, 1L); // Unlike
        assertFalse(poll.getLikedUsers().contains(user)); // Verify user is removed
    }
    @Test
    void testSearchPollsByName_NoResults() {
        String query = "NonExistentPoll";
        when(pollRepository.findByNameContainingIgnoreCase(query)).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.searchPollsByName(query);
        assertTrue(result.isEmpty()); // Verify no results are returned
    }
    @Test
    void testGetPollsByUser_NoResults() {
        User user = new User();
        user.setId(1L);

        when(pollRepository.findByUser(user)).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getPollsByUser(user);
        assertTrue(result.isEmpty()); // Verify no results are returned
    }
    @Test
    void testGetAllPollsSortedByLikesAsc_NoResults() {
        when(pollRepository.findAllByOrderByLikesAsc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByLikesAsc();
        assertTrue(result.isEmpty()); // Verify no results are returned
    }

    @Test
    void testGetAllPollsSortedByLikesDesc_NoResults() {
        when(pollRepository.findAllByOrderByLikesDesc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByLikesDesc();
        assertTrue(result.isEmpty()); // Verify no results are returned
    }
    @Test
    void testGetAllPollsSortedByCreatedAtAsc_NoResults() {
        when(pollRepository.findAllByOrderByCreatedAtAsc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByCreatedAtAsc();
        assertTrue(result.isEmpty()); // Verify no results are returned
    }

    @Test
    void testGetAllPollsSortedByCreatedAtDesc_NoResults() {
        when(pollRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByCreatedAtDesc();
        assertTrue(result.isEmpty()); // Verify no results are returned
    }

}



//import finki.ukim.mk.surveyKing.model.*;
//import finki.ukim.mk.surveyKing.repository.PollRepository;
//import finki.ukim.mk.surveyKing.repository.UserRepository;
//import finki.ukim.mk.surveyKing.service.PollService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PollServiceTest {
//
//    @Mock
//    private PollRepository pollRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private PollService pollService;
//    private Poll mockPoll;
//    private User mockUser;
//
//    @BeforeEach
//    void setUp() {
//        mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setUsername("testUser");
//        mockPoll = new Poll();
//        mockPoll.setId(1);
//        mockPoll.setName("Test Survey");
//        mockPoll.setUser(mockUser);
//
//        lenient().when(pollRepository.findById(1)).thenReturn(Optional.of(mockPoll));
//        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
//        lenient().when(pollRepository.save(any(Poll.class))).thenAnswer(invocation -> invocation.getArgument(0));
//    }
//
//    @Test
//    void testGetAllPolls() {
//        when(pollRepository.findAll()).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.getAllPols();
//        assertEquals(1, polls.size());
//        assertEquals("Test Survey", polls.get(0).getName());
//    }
//
//    @Test
//    void testGetPollById() {
//        Poll poll = pollService.getPollById(1);
//        assertNotNull(poll);
//        assertEquals(1, poll.getId());
//        assertEquals("Test Survey", poll.getName());
//    }
//
//    @Test
//    void testCreatePoll() {
//        Poll newPoll = new Poll();
//        newPoll.setId(2);
//        newPoll.setName("Testsurvey 2");
//        assertDoesNotThrow(() -> pollService.createPoll(newPoll));
//        verify(pollRepository, times(1)).save(newPoll);
//    }
//
//    @Test
//    void testDeletePoll() {
//        doNothing().when(pollRepository).deleteById(1);
//        assertDoesNotThrow(() -> pollService.deletePoll(1));
//        verify(pollRepository, times(1)).deleteById(1);
//    }
//
//    @Test
//    void testLikePollAddLike() {
//        pollService.likePoll(1, 1L);
//        assertTrue(mockPoll.getLikedUsers().contains(mockUser));
//        verify(pollRepository, times(1)).save(mockPoll);
//    }
//
//    @Test
//    void testLikePollRemoveLike() {
//        mockPoll.getLikedUsers().add(mockUser);
//        pollService.likePoll(1, 1L);
//        assertFalse(mockPoll.getLikedUsers().contains(mockUser));
//        verify(pollRepository, times(1)).save(mockPoll);
//    }
//
//    @Test
//    void testSearchPollsByName() {
//        when(pollRepository.findByNameContainingIgnoreCase("Survey")).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.searchPollsByName("Survey");
//        assertEquals(1, polls.size());
//        assertEquals("Test Survey", polls.get(0).getName());
//    }
//
//    @Test
//    void testGetPollsByUser() {
//        when(pollRepository.findByUser(mockUser)).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.getPollsByUser(mockUser);
//        assertEquals(1, polls.size());
//        assertEquals(mockUser, polls.get(0).getUser());
//    }
//
//    @Test
//    void testGetAllPollsSortedByLikesAsc() {
//        when(pollRepository.findAllByOrderByLikesAsc()).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.getAllPollsSortedByLikesAsc();
//        assertEquals(1, polls.size());
//    }
//
//    @Test
//    void testGetAllPollsSortedByLikesDesc() {
//        when(pollRepository.findAllByOrderByLikesDesc()).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.getAllPollsSortedByLikesDesc();
//        assertEquals(1, polls.size());
//    }
//
//    @Test
//    void testGetAllPollsSortedByCreatedAtAsc() {
//        when(pollRepository.findAllByOrderByCreatedAtAsc()).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.getAllPollsSortedByCreatedAtAsc();
//        assertEquals(1, polls.size());
//    }
//
//    @Test
//    void testGetAllPollsSortedByCreatedAtDesc() {
//        when(pollRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(mockPoll));
//        List<Poll> polls = pollService.getAllPollsSortedByCreatedAtDesc();
//        assertEquals(1, polls.size());
//    }
//
//    @Test
//    void testGetParticipantCount() {
//        when(pollRepository.findDistinctParticipantsCountByPollId(1)).thenReturn(5);
//        int count = pollService.getParticipantCount(1);
//        assertEquals(5, count);
//    }
//}
