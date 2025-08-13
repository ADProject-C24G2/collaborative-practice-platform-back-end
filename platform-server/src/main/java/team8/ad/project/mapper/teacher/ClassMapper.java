package team8.ad.project.mapper.teacher;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import team8.ad.project.annotation.AutoFill;
import team8.ad.project.entity.dto.SubmissionDetailDTO;
import team8.ad.project.entity.dto.ViewQuestionDTO;
import team8.ad.project.entity.entity.*;
import team8.ad.project.entity.entity.Class;
import team8.ad.project.entity.vo.ClassVO;
import team8.ad.project.entity.vo.StudentVO;
import team8.ad.project.enumeration.OperationType;

import java.time.LocalDateTime;
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
     * Create Announcement
     * @param myAnnouncement
     */
    void insertAnnouncement(Announcement myAnnouncement);


    /**
     * Get Announcement
     * @param classId
     * @return
     */
    @Select("SELECT * from announcement where classId = #{classId}")
    List<Announcement> getAnnouncement(int classId);


    @Select("SELECT name from user where id = #{studentId}")
    String getStudentsName(Integer studentId);

    /**
     * get questions
     * @param viewQuestionDTO
     * @return
     */
    List<Question> selectQuestions(ViewQuestionDTO viewQuestionDTO, int offset, int count);


    /**
     * Insert assignment
     * @param assignment
     * @return
     */
    @Insert("INSERT INTO assignment (class_id, assignment_name, expire_time, create_time, whether_finish, finish_time) " +
            "VALUES (#{classId}, #{assignmentName}, #{expireTime}, #{createTime}, #{whetherFinish}, #{finishTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertAssignment(Assignment assignment);


    /**
     * Insert assignment and question
     * @param details
     * @return
     */
    @Insert("INSERT INTO assignment_details (assignment_id, question_id) VALUES (#{assignmentId}, #{questionId})")
    int insertAssignmentDetails(AssignmentDetails details);




    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    @Select(" select * from user where email = #{email}")
    User getByEmail(String email);

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    @Select(" select * from user where id = #{id}")
    User getById(Integer id);

    @Select("SELECT COUNT(*) from assignment where class_id = #{id} AND expire_time >= #{time}")
    int getOngoingAssignment(int id, LocalDateTime time);

    /**
     * Inserts a new question record into the 'qa' table.
     * @param question The question entity to be inserted.
     * @return The number of rows affected.
     */
    int insertQuestion(Question question);

    /**
     * Finds all assignments for a given class ID.
     */
    List<Assignment> findAssignmentsByClassId(@Param("classId") int classId);

    /**
     * Finds all submission details for a given assignment ID,
     * joining with the students table to get names.
     */
    List<SubmissionDetailDTO> findSubmissionDetailsByAssignmentId(@Param("assignmentId") int assignmentId);
}
