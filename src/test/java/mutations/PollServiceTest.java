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

        user = new User();
        user.setId(1L);

        Question question = new Question();
        question.setText("What is your favorite color?");
        question.setPoll(poll);

        Option option = new Option();
        option.setDescription("Red");
        option.setQuestion(question);
        question.getOptions().add(option);

        poll.getQuestions().add(question);

        QuestionData questionData = new QuestionData();
        questionData.setText("What is your favorite fruit?");

        OptionData optionData = new OptionData();
        optionData.setDescription("Apple");

        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

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
        QuestionData questionData = new QuestionData();
        questionData.setText("What is your favorite fruit?");

        OptionData optionData = new OptionData();
        optionData.setDescription("Apple");

        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals("Updated Poll", poll.getName());

        assertEquals("What is your favorite fruit?", poll.getQuestions().get(0).getText());

        assertEquals("Apple", poll.getQuestions().get(0).getOptions().get(0).getDescription());

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
        String query = "Test Poll";
        List<Poll> polls = List.of(poll);

        when(pollRepository.findByNameContainingIgnoreCase(query)).thenReturn(polls);

        List<Poll> result = pollService.searchPollsByName(query);

        assertEquals(1, result.size());
        assertEquals("Test Poll", result.get(0).getName());

        verify(pollRepository, times(1)).findByNameContainingIgnoreCase(query);
    }
    @Test
    void testFindPollsByUser() {
        User user = new User();
        user.setId(1L);

        List<Poll> polls = List.of(poll);

        when(pollRepository.findByUser(user)).thenReturn(polls);

        List<Poll> result = pollService.getPollsByUser(user);

        assertEquals(1, result.size());
        assertEquals("Test Poll", result.get(0).getName());

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
        Poll poll = new Poll();
        poll.setName("Initial Poll");
        poll.setQuestions(new ArrayList<>());

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
        assertEquals(2, poll.getQuestions().size());

        List<Question> updatedQuestions = poll.getQuestions();
        assertTrue(updatedQuestions.stream().anyMatch(q -> "What is your favorite color?".equals(q.getText())));
        assertTrue(updatedQuestions.stream().anyMatch(q -> "What is your favorite fruit?".equals(q.getText())));

        for (Question question : updatedQuestions) {
            assertFalse(question.getOptions().isEmpty(), "Each question must have options");

            assertNotNull(question.getPoll(), "Question's poll should not be null");
            assertEquals(poll, question.getPoll(), "Question's poll reference is incorrect");

            for (Option option : question.getOptions()) {
                assertNotNull(option.getDescription(), "Option's description should not be null");
                assertTrue(option.getDescription().equals("Red") || option.getDescription().equals("Apple"),
                        "Option's description is incorrect");

                assertNotNull(option.getQuestion(), "Option's question should not be null");
                assertEquals(question, option.getQuestion(), "Option's question reference is incorrect");

                assertNotNull(option.getPoll(), "Option's poll should not be null");
                assertEquals(poll, option.getPoll(), "Option's poll reference is incorrect");
            }
        }
    }
    @Test
    void testFindPollsByNameContainingIgnoreCase_NoResults() {
        String query = "NonExistentPoll";
        when(pollRepository.findByNameContainingIgnoreCase(query)).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.searchPollsByName(query);

        assertTrue(result.isEmpty());
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

        assertEquals(1, question.getOptions().size());
        assertEquals("Blue", question.getOptions().get(0).getDescription());
    }
    @Test
    void testLikePollByMultipleUsers() {
        User user2 = new User();
        user2.setId(2L);

        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        pollService.likePoll(1, 1L);
        pollService.likePoll(1, 2L);

        assertTrue(poll.getLikedUsers().contains(user));
        assertTrue(poll.getLikedUsers().contains(user2));
        assertEquals(2, poll.getLikedUsers().size());
    }
    @Test
    void testLikePollWhenAlreadyLiked() {
        int pollId = 1;
        Long userId = 1L;

        Poll poll = new Poll();
        User user = new User();
        poll.getLikedUsers().add(user);

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        pollService.likePoll(pollId, userId);

        assertFalse(poll.getLikedUsers().contains(user), "User should no longer be in the likedUsers list");

        verify(pollRepository).save(poll);
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
        List<Option> newOptionsData = new ArrayList<>(List.of(option1, option2));

        if (existingOptions.size() > newOptionsData.size()) {
            existingOptions.subList(newOptionsData.size(), existingOptions.size()).clear();
        }

        assertEquals(2, existingOptions.size());
        assertEquals("A", existingOptions.get(0).getDescription());
        assertEquals("B", existingOptions.get(1).getDescription());
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        Long nonExistentUserId = 999L;
        when(userRepository.findByUsername(String.valueOf(nonExistentUserId))).thenReturn(Optional.empty());

        User result = userService.findByUsername(String.valueOf(nonExistentUserId));

        assertNull(result);
    }
    @Test
    void testUpdatePoll_SetsName() {
        PollData pollData = new PollData();
        pollData.setName("Updated Poll");

        pollService.updatePoll(poll, pollData);
        assertEquals("Updated Poll", poll.getName());
    }

    @Test
    void testLikePoll_AddsUser() {
        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        pollService.likePoll(1, 1L);
        assertTrue(poll.getLikedUsers().contains(user));
    }

    @Test
    void testLikePoll_RemovesUser() {
        when(pollRepository.findById(1)).thenReturn(Optional.of(poll));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        pollService.likePoll(1, 1L);
        pollService.likePoll(1, 1L);
        assertFalse(poll.getLikedUsers().contains(user));
    }
    @Test
    void testSearchPollsByName_NoResults() {
        String query = "NonExistentPoll";
        when(pollRepository.findByNameContainingIgnoreCase(query)).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.searchPollsByName(query);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetPollsByUser_NoResults() {
        User user = new User();
        user.setId(1L);

        when(pollRepository.findByUser(user)).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getPollsByUser(user);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetAllPollsSortedByLikesAsc_NoResults() {
        when(pollRepository.findAllByOrderByLikesAsc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByLikesAsc();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllPollsSortedByLikesDesc_NoResults() {
        when(pollRepository.findAllByOrderByLikesDesc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByLikesDesc();
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetAllPollsSortedByCreatedAtAsc_NoResults() {
        when(pollRepository.findAllByOrderByCreatedAtAsc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByCreatedAtAsc();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllPollsSortedByCreatedAtDesc_NoResults() {
        when(pollRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());

        List<Poll> result = pollService.getAllPollsSortedByCreatedAtDesc();
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdatePoll_QuestionPollIsSet() {
        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals(poll, poll.getQuestions().get(0).getPoll());
    }
    @Test
    void testUpdatePoll_OptionDescriptionIsSet() {
        OptionData optionData = new OptionData();
        optionData.setDescription("New Option Description");

        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");
        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals("New Option Description", poll.getQuestions().get(0).getOptions().get(0).getDescription());
    }
    @Test
    void testUpdatePoll_OptionQuestionIsSet() {
        OptionData optionData = new OptionData();
        optionData.setDescription("New Option");

        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");
        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals(poll.getQuestions().get(0), poll.getQuestions().get(0).getOptions().get(0).getQuestion());
    }
    @Test
    void testUpdatePoll_OptionPollIsSet() {
        Poll poll = new Poll();
        poll.setId(1);
        poll.setName("Test Poll");

        Question question = new Question();
        question.setId(1);
        question.setText("What is your favorite color?");
        question.setPoll(poll);

        Option option = new Option();
        option.setId(1);
        option.setDescription("Red");
        option.setQuestion(question);
        option.setPoll(poll);

        question.getOptions().add(option);

        poll.getQuestions().add(question);

        OptionData optionData = new OptionData();
        optionData.setDescription("Blue");

        QuestionData questionData = new QuestionData();
        questionData.setText("What is your favorite fruit?");
        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        when(pollRepository.save(any(Poll.class))).thenReturn(poll);

        pollService.updatePoll(poll, pollData);

        Option updatedOption = poll.getQuestions().get(0).getOptions().get(0);

        assertNotNull(updatedOption.getPoll(), "Option's poll should not be null");
        assertEquals(poll, updatedOption.getPoll(), "Option's poll should match the parent poll");

        System.out.println("Updated Option: " + updatedOption.getDescription() + ", Poll: " + updatedOption.getPoll());
    }
    @Test
    void testUpdatePoll_NegatedConditional() {
        Poll poll = new Poll();
        poll.setName("Initial Poll");
        poll.setQuestions(new ArrayList<>());

        QuestionData questionData = new QuestionData();
        questionData.setText("What is your favorite animal?");
        OptionData optionData = new OptionData();
        optionData.setDescription("Dog");
        questionData.setOptions(List.of(optionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll with New Question");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals("Updated Poll with New Question", poll.getName());
        assertEquals(1, poll.getQuestions().size());

        Question updatedQuestion = poll.getQuestions().get(0);
        assertEquals("What is your favorite animal?", updatedQuestion.getText());

        assertFalse(updatedQuestion.getOptions().isEmpty(), "The question must have options");
        Option updatedOption = updatedQuestion.getOptions().get(0);

        assertNotNull(updatedOption.getDescription(), "Option's description should not be null");
        assertEquals("Dog", updatedOption.getDescription(), "Option's description is incorrect");

        assertNotNull(updatedOption.getQuestion(), "Option's question should not be null");
        assertEquals(updatedQuestion, updatedOption.getQuestion(), "Option's question reference is incorrect");

        assertNotNull(updatedOption.getPoll(), "Option's poll should not be null");
        assertEquals(poll, updatedOption.getPoll(), "Option's poll reference is incorrect");
    }

    @Test
    void testUpdatePoll_ChangedConditionalBoundary() {
        Question existingQuestion = new Question();
        existingQuestion.setText("Old Question");
        existingQuestion.setPoll(poll);

        Option existingOption = new Option();
        existingOption.setDescription("Old Option");
        existingOption.setQuestion(existingQuestion);

        existingQuestion.getOptions().add(existingOption);
        poll.getQuestions().add(existingQuestion);

        OptionData newOptionData = new OptionData();
        newOptionData.setDescription("New Option");

        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");
        questionData.setOptions(List.of(newOptionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals(1, poll.getQuestions().get(0).getOptions().size());
    }
    @Test
    void testUpdatePoll_ListClearIsCalled() {
        Question existingQuestion = new Question();
        existingQuestion.setText("Old Question");
        existingQuestion.setPoll(poll);

        Option existingOption1 = new Option();
        existingOption1.setDescription("Old Option 1");
        existingOption1.setQuestion(existingQuestion);

        Option existingOption2 = new Option();
        existingOption2.setDescription("Old Option 2");
        existingOption2.setQuestion(existingQuestion);

        existingQuestion.getOptions().add(existingOption1);
        existingQuestion.getOptions().add(existingOption2);
        poll.getQuestions().add(existingQuestion);

        OptionData newOptionData = new OptionData();
        newOptionData.setDescription("New Option");

        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");
        questionData.setOptions(List.of(newOptionData));

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals(1, poll.getQuestions().get(0).getOptions().size());
    }
    @Test
    void testUpdatePoll_ChangedConditionalBoundaryForQuestions() {
        Question existingQuestion = new Question();
        existingQuestion.setText("Old Question");
        existingQuestion.setPoll(poll);

        poll.getQuestions().add(existingQuestion);

        QuestionData questionData = new QuestionData();
        questionData.setText("New Question");

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals(2, poll.getQuestions().size());
    }
    @Test
    void testLikePoll_PollNotFound() {
        int pollId = 1;
        Long userId = 1L;

        when(pollRepository.findById(pollId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pollService.likePoll(pollId, userId);
        });

        assertEquals("Poll not found", exception.getMessage());

        verify(userRepository, never()).findById(any());
    }

    @Test
    void testLikePoll_UserNotFound() {
        int pollId = 1;
        Long userId = 1L;

        Poll poll = new Poll();
        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pollService.likePoll(pollId, userId);
        });

        assertEquals("User not found", exception.getMessage());

        verify(pollRepository).findById(pollId);
    }
    @Test
    void testGetParticipantCount() {
        when(pollRepository.findDistinctParticipantsCountByPollId(1)).thenReturn(5);

        int result = pollService.getParticipantCount(1);
        assertEquals(5, result);
    }
    @Test
    void testUpdatePoll_QuestionTextIsSet() {
        QuestionData questionData = new QuestionData();
        questionData.setText("New Question Text");

        PollData pollData = new PollData();
        pollData.setName("Updated Poll");
        pollData.setQuestions(List.of(questionData));

        pollService.updatePoll(poll, pollData);

        assertEquals("New Question Text", poll.getQuestions().get(0).getText());
    }

}