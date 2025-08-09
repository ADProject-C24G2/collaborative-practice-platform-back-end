package team8.ad.project.service.teacher;

import team8.ad.project.entity.dto.AnnouncementDTO;
import team8.ad.project.entity.vo.ClassVO;
import team8.ad.project.entity.vo.StudentVO;
import team8.ad.project.entity.vo.TeacherVO;
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
}
