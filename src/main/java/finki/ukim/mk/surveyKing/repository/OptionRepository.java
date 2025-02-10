package finki.ukim.mk.surveyKing.repository;

import finki.ukim.mk.surveyKing.model.Option;
import finki.ukim.mk.surveyKing.model.Poll;
import finki.ukim.mk.surveyKing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option,Long> {
    Optional<Option> findById(Long id);
    @Query("SELECT o FROM Option o JOIN o.users u WHERE o.poll = :poll AND u = :user")
    List<Option> findSelectedOptionsByPollAndUser(@Param("poll") Poll poll, @Param("user") User user);
}
