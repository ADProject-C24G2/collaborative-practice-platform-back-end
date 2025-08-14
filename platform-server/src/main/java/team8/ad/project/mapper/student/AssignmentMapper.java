package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team8.ad.project.entity.dto.AssignmentItemDTO;

import java.util.List;

@Mapper
public interface AssignmentMapper {

    // 查询班级名（用于响应头部）
    @Select("SELECT id AS classId, name AS className FROM class WHERE id = #{classId}")
    ClassHeader selectClassHeader(@Param("classId") Integer classId);

    // 查该班所有作业
    @Select("SELECT " +
        "a.id AS assignmentId, " +
        "a.assignment_name AS assignmentName, " +
        "a.expire_time AS expireTime, " +
        "asd.whetherFinish AS whetherFinish, " +
        "asd.finishTime AS finishTime " +
        "FROM assignment a " +
        "LEFT JOIN assignment_students_details asd " +
        "ON asd.assignmentId = a.id " +
        "AND asd.studentId = #{studentId} " +
        "WHERE a.class_id = #{classId} " +
        "ORDER BY a.create_time DESC")
    List<AssignmentItemDTO> listByClassId(@Param("classId") Integer classId,
                                          @Param("studentId") Long studentId);
    // 内部用的小 DTO（只拿 classId/className）
    class ClassHeader {
        public Integer classId;
        public String  className;
    }
}