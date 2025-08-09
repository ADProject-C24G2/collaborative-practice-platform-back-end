package team8.ad.project.service.teacher.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.AnnouncementDTO;
import team8.ad.project.entity.entity.Announcement;
import team8.ad.project.entity.vo.ClassVO;
import team8.ad.project.entity.vo.StudentVO;
import team8.ad.project.entity.vo.TeacherVO;
import team8.ad.project.entity.dto.ClassDTO;
import team8.ad.project.entity.entity.Tag;
import team8.ad.project.entity.entity.User;
import team8.ad.project.mapper.teacher.ClassMapper;
import team8.ad.project.service.teacher.ClassService;
import team8.ad.project.entity.entity.Class;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class ClassServiceImpl implements ClassService {
// TODO 这里的数据是模拟的
    private final Random random = new Random();


    @Autowired
    ClassMapper classMapper;

    /**
     *Create a new Class
     * @param classDTO
     */
    @Override
    public String createClass(ClassDTO classDTO) {
        String token = null;
        Class myClass = new Class();
        BeanUtils.copyProperties(classDTO, myClass);

        if(classDTO.getAccessType().equals("byLink") ){
            token = UUID.randomUUID().toString();
            myClass.setToken(token);
        }
            setTime(classDTO,myClass);
            myClass.setTeacherId(BaseContext.getCurrentId());
            classMapper.insert(myClass);
        return token;

    }

    /**
     * Get teacher profile
     *
     * @return TeacherVO
     */
    @Override
    public TeacherVO getTeacherProfile(){
        int currentUserId = BaseContext.getCurrentId();
        User user = classMapper.getTeacherProfile(currentUserId);
        if (user == null) {
            // 处理用户不存在的情况
            log.error("Teacher not found for ID: {}", currentUserId);
            return null; // 或者抛出异常
        }
        TeacherVO teacherVO = new TeacherVO();
        BeanUtils.copyProperties(user,teacherVO);
        teacherVO.setUserid(String.valueOf(user.getId()));

        List<Tag> tags = classMapper.getTagsByTeacherId(currentUserId);
        teacherVO.setTags(tags);

        log.info("Successfully fetched profile for teacher: {}", teacherVO.getName());
        return teacherVO;
    }



    @Override
    public List<ClassVO> getClassList(int teacherId) {
        log.info("Fetching class list for teacher ID: {}", teacherId);

        try {
            // 1. 调用 Mapper 获取 Class 实体列表
            List<Class> classList = classMapper.selectClassListByTeacherId(teacherId);
            log.debug("Retrieved {} classes from database for teacher ID: {}", classList.size(), teacherId);

            // 2. 创建用于返回的 ClassVO 列表
            List<ClassVO> classVOList = new ArrayList<>(classList.size());

            // 3. 遍历 Class 实体列表，转换为 ClassVO
            for (Class clazz : classList) {
                ClassVO vo = new ClassVO();

                // 4. 手动映射字段
                vo.setId(String.valueOf(clazz.getId())); // int -> String
                vo.setClassName(clazz.getName());

                // *** 修改点: 从 UserClassDetails 表查询学生数 ***
                int studentCount = classMapper.countStudentsInClass(clazz.getId());
                vo.setStudentAmount(studentCount);
                log.trace("Fetched student count: {} for class ID: {}", studentCount, clazz.getId());

                // *** 修改点 3: 使用假数据设置 unreadMessages ***
                // TODO 这里还有unread的message没有设置
                vo.setUnreadMessages(random.nextInt(10));
                log.trace("Assigned random unreadMessages: {} for class ID: {}", vo.getUnreadMessages(), clazz.getId());


                // TODO 图片没设置
                // 5. 设置 avatar
//                String avatarUrl = getDefaultAvatarForClassName(clazz.getName());
                String avatarUrl = "";
                vo.setAvatar(avatarUrl);
                log.trace("Assigned avatar: {} for class ID: {}", avatarUrl, clazz.getId());

                // 6. 将转换好的 VO 添加到列表
                classVOList.add(vo);
            }

            log.info("Successfully converted {} classes to ClassVOs for teacher ID: {}", classVOList.size(), teacherId);
            return classVOList;

        } catch (Exception e) {
            log.error("Error occurred while fetching or converting class list for teacher ID: {}", teacherId, e);
            return new ArrayList<>(); // 返回空列表
        }
    }


    /**
     * get students in specific class
     * @param classId
     * @return
     */
    @Override
    public List<StudentVO> getStudents(int classId) {
        List<StudentVO> studentVOList = classMapper.getStudents(classId);
        return studentVOList;
    }


    /**
     * Create AnnouncementDTO
     * @param announcementDTO
     */
    @Override
    public void inserAnnouncement(AnnouncementDTO announcementDTO) {
        Announcement myAnnouncement = new Announcement();
        BeanUtils.copyProperties(announcementDTO, myAnnouncement);
        myAnnouncement.setStatus((byte)0);
        myAnnouncement.setTeacherId(BaseContext.getCurrentId());
        myAnnouncement.setClassId(Integer.parseInt(announcementDTO.getClassId()));
        myAnnouncement.setCreateTime(LocalDateTime.now());
        if(announcementDTO.getRecipientType().equals("specific")){
            for(int i : announcementDTO.getSpecificRecipients()){
                myAnnouncement.setStudentId(i);
                classMapper.insertAnnouncement(myAnnouncement);
                log.info("successfully inserted announcement: {}", myAnnouncement);
            }
        }
        else{
            List<StudentVO> studentsId = classMapper.getStudents(Integer.parseInt(announcementDTO.getClassId()));
            for(StudentVO studentVO : studentsId){
                myAnnouncement.setStudentId(studentVO.getStudentId());
                classMapper.insertAnnouncement(myAnnouncement);
            }
        }




    }


    private void setTime(ClassDTO classDTO, Class myClass){
        List<String> dateRange = classDTO.getDate();
        if (dateRange != null && dateRange.size() == 2) {
            try {
                // 解析开始时间（访问生效时间）
                LocalDateTime available = LocalDateTime.parse(
                        dateRange.get(0) + "T00:00:00",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                );
                myClass.setAccessAvailable(available);

                // 解析结束时间（访问过期时间）
                LocalDateTime expiration = LocalDateTime.parse(
                            dateRange.get(1) + "T23:59:59",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                );
                myClass.setAccessExpiration(expiration);

            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + dateRange, e);
            }
        } else {
            throw new IllegalArgumentException("Date range must contain exactly two values: [start, end]");
        }

        }

    }



