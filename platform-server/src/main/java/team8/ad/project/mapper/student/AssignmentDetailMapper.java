package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository("studentAssignmentDetailMapper")
public interface AssignmentDetailMapper {

    @Select("SELECT question_id FROM assignment_details " +
            "WHERE assignment_id = #{assignmentId} " +
            "ORDER BY id ASC")
    List<Integer> listQuestionIdsByAssignment(@Param("assignmentId") Integer assignmentId);
}
