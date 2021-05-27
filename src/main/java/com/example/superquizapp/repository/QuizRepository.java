package com.example.superquizapp.repository;

import com.example.superquizapp.domain.Quiz;
import com.example.superquizapp.domain.Category;
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

        @Query("select reviewer_id from Quiz where id =:id")
        String fetchReviewer(@Param("id") Long id);

        @Query(value = "SELECT id FROM Quiz where quizIndex =:id")
        Long getQuizIdByIndex(@Param("id") int id);
        @Query(value = "SELECT id FROM Quiz where surveyIndex =:id")
        Long getSurveyIdByIndex(@Param("id") int id);
        @Query("From Quiz where reviewer_id =:reviewerId")
        List<Quiz> fetchReviewingQuizById(@Param("reviewerId") Long reviewerId);
        @Query("From Quiz where invigilator_id =:invigilatorId")
        List<Quiz> fetchInvigilatingQuizById(@Param("invigilatorId") Long invigilatorId);

        @Query("SELECT distinct(category) FROM Quiz")
        List<Category> getDistinctCategories();
        @Query("SELECT count(id) FROM Quiz where category.category_id=:catId")
        Long getBankPerCategory(@Param("catId") Long catId);

        @Modifying
        @Query("UPDATE Question set blobUrl=:s1   where quiz_id =:id and id=:s ")
        void updateBlobUrl(@Param("id") Long id, @Param("s") Long s , @Param("s1") String s1);
}
