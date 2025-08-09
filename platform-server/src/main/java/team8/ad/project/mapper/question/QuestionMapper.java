package team8.ad.project.mapper.question;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import team8.ad.project.entity.dto.QsInform;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.entity.entity.AnswerRecord;
import team8.ad.project.entity.entity.Question;
import team8.ad.project.entity.entity.StudentRecommendation;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface QuestionMapper {
    List<QsInform> viewQuestion(
        @Param("keyword") String keyword, 
        @Param("questionName") String questionName,
        @Param("grade") String grade,
        @Param("subject") String subject,
        @Param("topic") String topic,
        @Param("category") String category,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    int getTotalCount(
        @Param("keyword") String keyword,
        @Param("questionName") String questionName,
        @Param("grade") String grade,
        @Param("subject") String subject,
        @Param("topic") String topic,
        @Param("category") String category
    );

    // @Select("SELECT id, image, question, choices, answer FROM qa WHERE id = #{id}")
    // SelectQuestionDTO getQuestionById(@Param("id") int id);

    @Select("SELECT id, image, question, choices, answer FROM qa WHERE id = #{id}")
    Question selectById(@Param("id") int id);

    @Insert("INSERT INTO student_answer_record (student_id, question_id, is_correct, answer) " +
        "VALUES (#{record.studentId}, #{record.questionId}, #{record.isCorrect}, #{record.answer})")int saveAnswerRecord(@Param("record") AnswerRecord record);

    @Select("SELECT * FROM student_answer_record WHERE student_id = #{studentId} AND DATE(timestamp) = #{date}")
    List<AnswerRecord> getRecordsByStudentAndDate(@Param("studentId") Long studentId, @Param("date") LocalDate date);

    @Select("SELECT * FROM student_answer_record WHERE student_id = #{studentId}")
    List<AnswerRecord> getRecordsByStudent(@Param("studentId") Long studentId);

    @Insert("INSERT INTO student_recommendations (student_id, recommended_questions) VALUES (#{recommendation.studentId}, #{recommendation.recommendedQuestions})")
    int saveRecommendedQuestions(@Param("recommendation") StudentRecommendation recommendation);

    @Update("UPDATE student_recommendations SET recommended_questions = #{recommendation.recommendedQuestions} WHERE student_id = #{recommendation.studentId}")
    int updateRecommendedQuestions(@Param("recommendation") StudentRecommendation recommendation);

    @Select("SELECT COUNT(*) FROM student_recommendations WHERE student_id = #{studentId}")
    int countRecommendationsByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM student_recommendations WHERE student_id = #{studentId}")
    StudentRecommendation getRecommendationByStudentId(@Param("studentId") Long studentId);
}