package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import team8.ad.project.entity.entity.AssignmentStudentsDetails;

@Mapper
@Repository("studentAssignmentStudentsDetailsMapper")
public interface AssignmentStudentsDetailsMapper {

    /**
     * 主键(assignmentId, studentId) 冲突则更新
     */
    @Insert(
        "INSERT INTO assignment_students_details " +
        "(assignmentId, studentId, whetherFinish, finishTime, Accurancy) " +
        "VALUES (#{assignmentId}, #{studentId}, #{whetherFinish}, #{finishTime}, #{accurancy}) " +
        "ON DUPLICATE KEY UPDATE " +
        "whetherFinish = VALUES(whetherFinish), " +
        "finishTime    = VALUES(finishTime), " +
        "Accurancy     = VALUES(Accurancy)"
    )
    int upsert(AssignmentStudentsDetails row);
}
