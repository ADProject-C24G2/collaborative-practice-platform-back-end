package team8.ad.project.service.teacher;

import team8.ad.project.entity.dto.AnnouncementDTO;
import team8.ad.project.entity.dto.ViewQuestionDTO;
import team8.ad.project.entity.vo.*;
import team8.ad.project.entity.dto.ClassDTO;

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


    void inserAnnouncement(AnnouncementDTO announcementDTO);

    List<AnnouncementVO> getAnnouncement(int classId);

    List<QuestionVO> getQuestions(ViewQuestionDTO viewQuestionDTO);
}
