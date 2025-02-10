package finki.ukim.mk.surveyKing.repository;

import finki.ukim.mk.surveyKing.model.Poll;
import finki.ukim.mk.surveyKing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll,Integer> {
    List<Poll> findByUser(User user);
    List<Poll> findByNameContainingIgnoreCase(String name);
    @Query("SELECT p FROM Poll p LEFT JOIN p.likedUsers u GROUP BY p ORDER BY COUNT(u) ASC")
    List<Poll> findAllByOrderByLikesAsc();

    @Query("SELECT p FROM Poll p LEFT JOIN p.likedUsers u GROUP BY p ORDER BY COUNT(u) DESC")
    List<Poll> findAllByOrderByLikesDesc();

    @Query("SELECT p FROM Poll p ORDER BY p.createdAt ASC")
    List<Poll> findAllByOrderByCreatedAtAsc();

    @Query("SELECT p FROM Poll p ORDER BY p.createdAt DESC")
    List<Poll> findAllByOrderByCreatedAtDesc();

    @Query(value = "SELECT COUNT(DISTINCT user_id) FROM user_completed_polls WHERE poll_id = :pollId", nativeQuery = true)
    int findDistinctParticipantsCountByPollId(@Param("pollId") int pollId);
}
