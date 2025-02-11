package mutations;


import finki.ukim.mk.surveyKing.model.Poll;
import finki.ukim.mk.surveyKing.model.Question;
import finki.ukim.mk.surveyKing.repository.QuestionRepository;
import finki.ukim.mk.surveyKing.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;
    private Question mockQuestion;
    private Poll mockPoll;

    @BeforeEach
    void setUp() {
        mockPoll = new Poll();
        mockPoll.setId(1);

        mockQuestion = new Question();
        mockQuestion.setId(1);
        mockQuestion.setPoll(mockPoll);
        mockQuestion.setText("Test question?");

        lenient().when(questionRepository.findById(1L)).thenReturn(Optional.of(mockQuestion));
        lenient().when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testGetQuestionById() {
        Question result = questionService.getQuestionById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test question?", result.getText());
    }

    @Test
    void testGetQuestionsByPollId() {
        when(questionRepository.findAll()).thenReturn(Arrays.asList(mockQuestion));
        List<Question> result = questionService.getQuestionsByPollId(1);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getPoll().getId());
    }

    @Test
    void testCreateQuestion() {
        Question newQuestion = new Question();
        newQuestion.setId(2);
        newQuestion.setPoll(mockPoll);
        newQuestion.setText("Test q?");
        Question savedQuestion = questionService.createQuestion(newQuestion);
        assertNotNull(savedQuestion);
        assertEquals("Test q?", savedQuestion.getText());
    }

    @Test
    void testDeleteQuestion() {
        doNothing().when(questionRepository).deleteById(1L);
        assertDoesNotThrow(() -> questionService.deleteQuestion(1));
        verify(questionRepository, times(1)).deleteById(1L);
    }
    @Test
    void testGetQuestionsByPollId_FilteringWorks() {
        Poll otherPoll = new Poll();
        otherPoll.setId(2);

        Question question2 = new Question();
        question2.setId(2);
        question2.setPoll(otherPoll);
        question2.setText("Another question?");

        when(questionRepository.findAll()).thenReturn(Arrays.asList(mockQuestion, question2));

        List<Question> result = questionService.getQuestionsByPollId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getPoll().getId());
    }

    @Test
    void testGetQuestionById_NotFound() {
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            questionService.getQuestionById(99);
        });

        assertEquals("Question not found", exception.getMessage());
    }

}
