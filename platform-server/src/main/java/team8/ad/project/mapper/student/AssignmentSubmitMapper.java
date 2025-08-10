package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("studentAssignmentSubmitMapper")
public interface AssignmentSubmitMapper {

    // 只查需要的四个字段，按 assignment_details.id 顺序返回
    @Select("SELECT q.id, q.image, q.question, q.choices " +
            "FROM assignment_details d " +
            "JOIN qa q ON q.id = d.question_id " +
            "WHERE d.assignment_id = #{assignmentId} " +
            "ORDER BY d.id ASC")
    java.util.List<team8.ad.project.entity.entity.Question> listRawQuestionsByAssignment(@Param("assignmentId") Integer assignmentId);
}