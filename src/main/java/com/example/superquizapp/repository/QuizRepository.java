package com.example.superquizapp.repository;

import com.example.superquizapp.domain.Quiz;
import com.example.superquizapp.domain.Topic;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long> {
    @Query("select assigneeUserList from Quiz where id =:id")
    String fetchAssigneeListById(@Param("id") Long id);
    @Query(value = "SELECT id FROM Quiz where quizIndex =:id")
    Long getQuizIdByIndex(@Param("id") int id);
    @Query("SELECT distinct(topic) FROM Quiz")
    List<Topic> getDistinctTopics();
    @Modifying
    @Query("UPDATE Question set blobUrl=:s1   where quiz_id =:id and id=:s ")
    void updateBlobUrl(@Param("id") Long id, @Param("s") Long s , @Param("s1") String s1);
    @Query("SELECT count(id) FROM Quiz where topic.topic_id=:topicId")
    Long getBankPerTopic(@Param("topicId") Long topicId);
}
