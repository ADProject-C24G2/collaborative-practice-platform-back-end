package team8.ad.project.mapper.student;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import team8.ad.project.entity.dto.ClassListItemDTO;
import java.util.List;

@Mapper
@Repository("studentClassMapper")
public interface ClassMapper {

    @Select("SELECT id AS classId, name AS className, description " +
            "FROM class")
    List<ClassListItemDTO> viewClass();

    // —— 新增：按 token 查班级（byLink）
    @Select("SELECT id, teacher_id AS teacherId, name, description, access_type AS accessType, token, " +
            "access_expiration AS accessExpiration, access_available AS accessAvailable, max_members AS maxMembers, " +
            "create_time AS createTime, update_time AS updateTime, create_user AS createUser, update_user AS updateUser " +
            "FROM class WHERE token = #{token} AND access_type = 'byLink'")
    team8.ad.project.entity.entity.Class selectByToken(@Param("token") String token);

    // —— 新增：按名称查班级（byName）
    @Select("SELECT id, teacher_id AS teacherId, name, description, access_type AS accessType, token, " +
            "access_expiration AS accessExpiration, access_available AS accessAvailable, max_members AS maxMembers, " +
            "create_time AS createTime, update_time AS updateTime, create_user AS createUser, update_user AS updateUser " +
            "FROM class WHERE name = #{name} AND access_type = 'byName'")
    team8.ad.project.entity.entity.Class selectByName(@Param("name") String name);

    // —— 新增：统计班级当前人数
    @Select("SELECT COUNT(*) FROM user_class_details WHERE class_id = #{classId}")
    int countMembers(@Param("classId") Integer classId);

    // —— 新增：是否已在班级
    @Select("SELECT COUNT(*) FROM user_class_details WHERE class_id = #{classId} AND student_id = #{studentId}")
    int existsMember(@Param("classId") Integer classId, @Param("studentId") Long studentId);

    // —— 新增：插入班级成员
    @Insert("INSERT INTO user_class_details (class_id, student_id, create_time) VALUES (#{classId}, #{studentId}, NOW())")
    int insertMember(@Param("classId") Integer classId, @Param("studentId") Long studentId);
}
