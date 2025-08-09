package team8.ad.project.mapper.teacher;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import team8.ad.project.annotation.AutoFill;
import team8.ad.project.entity.entity.Announcement;
import team8.ad.project.entity.entity.Class;
import team8.ad.project.entity.entity.Tag;
import team8.ad.project.entity.entity.User;
import team8.ad.project.entity.vo.ClassVO;
import team8.ad.project.entity.vo.StudentVO;
import team8.ad.project.enumeration.OperationType;

import java.util.List;

@Mapper
public interface ClassMapper {

    /**
     * Create class
     * @param newClass
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Class newClass);

    /**
     * Query all class
     * @return
     */
    @Select("select * from class")
    List<Class> selectAll();

    /**
     * Get teacher profile by ID
     * @param id The user ID of the teacher
     * @return The User entity
     */
    @Select("SELECT * FROM user WHERE id = #{id}") // 修正：使用 #{id}
    User getTeacherProfile(int id);

    /**
     * Get tags associated with a teacher by teacher ID (修正后的)
     * @param teacherId The user ID of the teacher
     * @return List of Tag entities
     */
    @Select("SELECT id, teacher_id, `key`, label FROM tag WHERE teacher_id = #{teacherId}") // 修正：使用 #{teacherId}，并指定列名
    List<Tag> getTagsByTeacherId(int teacherId); // 确保返回类型和导入正确


    /**
     * 根据教师ID查询其所有班级，并关联查询学生数量
     * @param teacherId The ID of the teacher
     * @return List of ClassVOs
     */
    @Select("SELECT * FROM class WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<Class> selectClassListByTeacherId(@Param("teacherId") int teacherId);

    /**
     * 统计指定班级的学生数量
     * @param classId The ID of the class
     * @return The number of students in the class
     */
    @Select("SELECT COUNT(*) FROM user_class_details WHERE class_id = #{classId}")
    int countStudentsInClass(@Param("classId") int classId);


    /**
     * Get the all the students id and name
     * @param classId
     * @return
     */
    @Select("SELECT u.id AS studentId, u.name AS studentName from user u left join user_class_details uc on u.id = uc.student_id where uc.class_id = #{classId} AND u.user_type != 'teacher'")
    List<StudentVO> getStudents(int classId);

    /**
     *
     * @param myAnnouncement
     */
    void insertAnnouncement(Announcement myAnnouncement);
}
