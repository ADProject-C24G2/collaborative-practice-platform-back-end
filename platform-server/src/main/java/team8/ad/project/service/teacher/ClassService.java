package team8.ad.project.service.teacher;

import team8.ad.project.entity.dto.*;
import team8.ad.project.entity.entity.User;
import team8.ad.project.entity.vo.*;
import team8.ad.project.result.Result;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;

public interface ClassService {

    /**
     * Create Class
     * @param classDTO
     */
    public String createClass(ClassDTO classDTO);

    public TeacherVO getTeacherProfile();


    public List<ClassVO> getClassList(int teacherId);

    List<StudentVO> getStudents(int classId);


    Result inserAnnouncement(AnnouncementDTO announcementDTO);

    List<AnnouncementVO> getAnnouncement(int classId);

    List<QuestionVO> getQuestions(ViewQuestionDTO viewQuestionDTO);

    Result makeAssignment(MakeAssignmentDTO dto) throws ParseException;

    LoginResultVO  login(LoginDTO loginDTO, HttpSession session);

    LoginResultVO logout(HttpSession session);

    Result<User> getCurrentUser(HttpSession session);

    void uploadQuestion(QuestionDTO questionDTO);

    List<AssignmentStatusVO> getAssignmentStatus(int classId);

    String register(RegisterDTO registerDTO);

    Result deleteAssignment(int assignmentId);
}
