package team8.ad.project.service.teacher.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import team8.ad.project.constant.UserConstant;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.*;
import team8.ad.project.entity.entity.*;
import team8.ad.project.entity.entity.Class;
import team8.ad.project.entity.vo.*;
import team8.ad.project.mapper.teacher.ClassMapper;
import team8.ad.project.result.Result;
import team8.ad.project.service.teacher.ClassService;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                int ongoingAssignment = classMapper.getOngoingAssignment(clazz.getId(),LocalDateTime.now());
                vo.setOngoingAssignment(ongoingAssignment);
                log.trace("Assigned random unreadMessages: {} for class ID: {}", vo.getOngoingAssignment(), clazz.getId());


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
    public Result inserAnnouncement(AnnouncementDTO announcementDTO) {
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
            if(studentsId.isEmpty()){
                return Result.error(3);
            }
            for(StudentVO studentVO : studentsId){
                myAnnouncement.setStudentId(studentVO.getStudentId());
                classMapper.insertAnnouncement(myAnnouncement);
            }
        }
        return Result.success(2);

    }


    /**
     * get Anncouncement
     * @param classId
     * @return
     */
    @Override
    public List<AnnouncementVO> getAnnouncement(int classId) {
        List<Announcement> announcements = classMapper.getAnnouncement(classId);
        List<AnnouncementVO> announcementVOList = new ArrayList<>();

        // 第一步：创建每个 Announcement 对应的 VO
        for (Announcement announcement : announcements) {
            AnnouncementVO announcementVO = new AnnouncementVO();
            BeanUtils.copyProperties(announcement, announcementVO);

            // 初始化 students 列表，避免 null
            announcementVO.setStudents(new ArrayList<>());

            if ("specific".equals(announcement.getRecipientType())) {
                AnnouncementVO.Student student = new AnnouncementVO.Student();
                student.setStudentId(announcement.getStudentId());
                String studentName = classMapper.getStudentsName(announcement.getStudentId());
                student.setStudentName(studentName);
                announcementVO.getStudents().add(student);
            }
            // 如果是 "all"，students 就是空列表

            announcementVOList.add(announcementVO);
        }

        // 第二步：按 createTime 合并（相同时间的公告合并为一个）
        // 因为不会有 all 和 specific 同时间，所以只需按 createTime 合并即可
        List<AnnouncementVO> result = new ArrayList<>();
        Set<LocalDateTime> processedTimes = new HashSet<>();

        for (AnnouncementVO vo : announcementVOList) {
            LocalDateTime time = vo.getCreateTime();

            if (processedTimes.contains(time)) {
                // 已处理过这个时间的公告，合并到已存在的 VO 中
                AnnouncementVO existing = result.stream()
                        .filter(v -> v.getCreateTime().equals(time))
                        .findFirst()
                        .orElse(null);

                if (existing != null && "specific".equals(vo.getRecipientType())) {
                    existing.getStudents().addAll(vo.getStudents());
                }
            } else {
                // 第一次遇到这个时间，直接加入结果
                result.add(vo);
                processedTimes.add(time);
            }
        }

        // 按 createTime 降序排序（最新在前）
        result.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

        return result;
    }


    /**
     * Get Questions
     * @param viewQuestionDTO
     * @return
     */
    @Override
    public List<QuestionVO> getQuestions(ViewQuestionDTO viewQuestionDTO) {
        int offset = (viewQuestionDTO.getPage() - 1) * viewQuestionDTO.getCount();
        List<Question> questions = classMapper.selectQuestions(viewQuestionDTO,offset,viewQuestionDTO.getCount());

        if (questions.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuestionVO> questionVOList = new ArrayList<>();
        for (Question question : questions) {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO, "choices");

            String choicesJson = question.getChoices();
            if (choicesJson != null && !choicesJson.isEmpty()) {
                try {
                    // ▼▼▼ 使用 Fastjson 进行解析 ▼▼▼
                    List<String> choiceList = JSON.parseArray(choicesJson, String.class);
                    questionVO.setChoices(choiceList);
                } catch (JSONException e) {
                    System.err.println("Failed to parse choices JSON with Fastjson: " + choicesJson);
                    e.printStackTrace();
                    questionVO.setChoices(Collections.emptyList());
                }
            } else {
                questionVO.setChoices(Collections.emptyList());
            }

            questionVOList.add(questionVO);
        }


        return questionVOList;
    }

    @Override
    public LoginResultVO login(LoginDTO loginDTO, HttpSession session){
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        User user = classMapper.getByEmail(email);

        // 1. 用户不存在
        if (user == null) {
            return buildLoginErrorResult();
        }

        // 2. 密码比对 (生产环境建议使用BCrypt)

        if (!password.equals(user.getPassword())) {
            return buildLoginErrorResult();
        }

        // 3. 权限校验，只允许老师登录
        if (!UserConstant.TEACHER_USER_TYPE.equals(user.getUserType())) {
            return buildLoginErrorResult();
        }

        // 4. 登录成功，将用户ID存入Session
        session.setAttribute(UserConstant.USER_ID_IN_SESSION, user.getId());

        // 5. 构建前端需要的成功返回格式
        return LoginResultVO.builder()
                .status("ok")
                .type(loginDTO.getType())
                .currentAuthority(user.getUserType())
                .build();

    }

    @Override
    public Result<User> getCurrentUser(HttpSession session) {
        Integer userId = (Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION);
        if (userId == null) {
            return Result.error("用户未登录");
        }

        User user = classMapper.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 出于安全考虑，不应该返回密码字段
        user.setPassword(null);

        return Result.success(user);
    }


    public void makeAssignment(MakeAssignmentDTO dto) throws ParseException {
        Assignment assignment = new Assignment();
        assignment.setClassId(Long.parseLong(dto.getClassId()));
        assignment.setAssignmentName(dto.getTitle());

        // Parse expire_time from ISO string to Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        assignment.setExpireTime(sdf.parse(dto.getExpire_time()));

        assignment.setCreateTime(new Date());
        assignment.setWhetherFinish(0); // Default to false
        assignment.setFinishTime(null); // Default to null

        // Insert assignment and get generated ID
        classMapper.insertAssignment(assignment);
        Long assignmentId = assignment.getId();

        // Insert assignment details for each question ID
        List<String> questionIds = dto.getQuestionIds();
        for (String questionIdStr : questionIds) {
            AssignmentDetails details = new AssignmentDetails();
            details.setAssignmentId(assignmentId);
            details.setQuestionId(Long.parseLong(questionIdStr));
            classMapper.insertAssignmentDetails(details);
        }
    }









    /**
     * 构建一个符合前端预期的登录失败响应 (已修改)
     * @return
     */
    private LoginResultVO buildLoginErrorResult() {
        return LoginResultVO.builder()
                .status("error")
                .type("account")
                .currentAuthority("guest") // 失败时权限为 guest
                .build();
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



