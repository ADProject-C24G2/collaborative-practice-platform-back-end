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
    @Select("SELECT id AS assignmentId, assignment_name AS assignmentName, " +
            "expire_time AS expireTime, whether_finish AS whetherFinish, finish_time AS finishTime " +
            "FROM assignment WHERE class_id = #{classId} ORDER BY create_time DESC")
    List<AssignmentItemDTO> listByClassId(@Param("classId") Integer classId);

    // 内部用的小 DTO（只拿 classId/className）
    class ClassHeader {
        public Integer classId;
        public String  className;
    }
}