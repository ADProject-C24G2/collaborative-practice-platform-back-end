package team8.ad.project.controller.teacher;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import team8.ad.project.constant.UserConstant;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.*;
import team8.ad.project.entity.entity.Announcement;
import team8.ad.project.entity.entity.User;
import team8.ad.project.entity.vo.*;
import team8.ad.project.result.Result;
import team8.ad.project.service.teacher.ClassService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher/class")
@Api(tags = "Class-related interfaces")
@Slf4j
public class ClassController {

    @Autowired
    @Qualifier("teacherClassService")
    private ClassService classService;
    /**
     * Create a new class
     * @param classDTO
     * @return
     */
    @PostMapping("/create")
    @ApiOperation("Create Class")
    public Result createClass(@RequestBody ClassDTO classDTO,HttpSession session){

        log.info("Create Class:{}", classDTO);
        BaseContext.setCurrentId((Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION));

        String token = classService.createClass(classDTO);

        return Result.success(token);

    }

    /**
     * Get Teacher Profile
     * @param
     * @return teacherVO
     */
    @GetMapping("/currentUserDetail")
    @ApiOperation("Get teacher profile")
    public Result getTeacherProfile(HttpSession session){
        log.info("Get teacher profile");
        BaseContext.setCurrentId((Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION));
        TeacherVO teacherVO = classService.getTeacherProfile();
        return Result.success(teacherVO);
    }

    /**
     * Get all the classes information
     * @param count
     * @return
     */
    @GetMapping("/class-list") // 新增的获取班级列表接口
    @ApiOperation("Obtain the list of classes for the teachers")
    public Result getClassList(@RequestParam(defaultValue = "30") int count,HttpSession session) {
        log.info("Get class list, requested count: {}", count);
        try {

            // 1. 获取当前登录教师的ID (假设已通过认证设置到 BaseContext)
            BaseContext.setCurrentId((Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION));
            int currentTeacherId = BaseContext.getCurrentId();
            log.debug("Current teacher ID from context: {}", currentTeacherId);

            List<ClassVO> classVOList = classService.getClassList(currentTeacherId);

            Map<String, Object> data = new HashMap<>();

            data.put("list", classVOList);

            return Result.success(data);
        } catch (Exception e) {
            log.error("Error fetching class list", e);
            return Result.error("获取班级列表失败: " + e.getMessage());
        }
    }


    /**
     * Get the specific students of the class
     * @param classId
     * @return
     */
    @GetMapping("/getStudents")
    @ApiOperation("get the specific students of the class")
    public Result getStudent(@RequestParam(defaultValue = "30") int classId,HttpSession session) {
        log.info("start to get students");
        BaseContext.setCurrentId((Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION));
        int currentTeacherId = BaseContext.getCurrentId();
        List<StudentVO> studentsVO = classService.getStudents(classId);
        return Result.success(studentsVO);
    }


    @PostMapping("/make-announcement")
    @ApiOperation("make announcement")
    public Result makeAnnouncement(@RequestBody AnnouncementDTO announcementDTO,HttpSession session) {
        log.info("start to make announcement");
        BaseContext.setCurrentId((Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION));
        return classService.inserAnnouncement(announcementDTO);
    }

    /**
     * get the class detials like students profiles and announcement
     * @param classId
     * @return
     */
    @GetMapping("/get-announcement")
    @ApiOperation("view the class details and manage")
    public Result manageClass(@RequestParam int classId,HttpSession session) {
        log.info("start to manage class");
        BaseContext.setCurrentId((Integer)session.getAttribute(UserConstant.USER_ID_IN_SESSION));
        List<AnnouncementVO> announcementVO = classService.getAnnouncement(classId);
        return Result.success(announcementVO);

    }


//    /**
//     * make assignment
//     * @param assignment
//     * @return
//     */
//    @PostMapping("/assign-assignment")
//    @ApiOperation("make assignment")
//    public Result assignAssignment(@RequestBody AllArguments.Assignment assignment) {
//        log.info("start to assign assignment");
//        return Result.success();
//    }

    /**
     * Get the questions
     * @param viewQuestionDTO
     * @return
     */
    @PostMapping("/get-questions")
    @ApiOperation("get questions")
    public Result getQuestions(@RequestBody ViewQuestionDTO viewQuestionDTO) {
        List<QuestionVO> questionVO = classService.getQuestions(viewQuestionDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("list", questionVO);
        return Result.success(data);
    }

    /**
     * make assignment
     * @param dto
     * @return
     */
    @PostMapping("/make-assignment")
    @ApiOperation("make-assignment form")
    public Result makeAssignment(@RequestBody MakeAssignmentDTO dto) {
        try {
            classService.makeAssignment(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error("Failed to create assignment: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * @param loginDTO
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public LoginResultVO login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return classService.login(loginDTO, session);
    }

    /**
     * 获取当前登录用户信息
     * @param session
     * @return
     */
    @GetMapping("/currentUser")
    @ApiOperation("获取当前用户信息")
    public Result<User> getCurrentUser(HttpSession session) {
        // 注意：前端期望的成功返回格式是 { data: { ...user_info... } }
        // 我们的 Result.success(user) 已经能生成这种格式了。
        return classService.getCurrentUser(session);
    }

    /**
     * upload question
     * @param quesionDTO
     * @return
     */
    @PostMapping("/upload-question")
    @ApiOperation("upload question")
    public Result uploadQuestion(@RequestBody QuestionDTO quesionDTO) {
        classService.uploadQuestion(quesionDTO);
        return Result.success();
    }

    /**
     * Get Student Assignment Status
     * @param classId
     * @return
     */
    @GetMapping("/assignment-status")
    @ApiOperation("Student Assignment Status")
    public Result assignmentStatus(@RequestParam int classId){
        List<AssignmentStatusVO> assignmentStatusVOS = classService.getAssignmentStatus(classId);
        return Result.success(assignmentStatusVOS);
    }

    /**
     * Register
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("Teacher Register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("Processing registration for user: {}", registerDTO.getName());
        try {
            // [!code focus:start]
            // 1. 调用service并接收返回值
            String errorMessage = classService.register(registerDTO);

            // 2. 判断返回值
            if (StringUtils.hasText(errorMessage)) {
                // 如果返回了错误信息，说明注册失败
                log.warn("Registration failed for {}: {}", registerDTO.getEmail(), errorMessage);
                return Result.error(4);
            }

            // 如果返回的是null或空字符串，说明注册成功
            return Result.success(5);
            // [!code focus:end]
        } catch (Exception e) {
            // 这个catch块仍然保留，用于捕获数据库连接失败等未预料到的系统级异常
            log.error("An unexpected error occurred during registration for user: {}", registerDTO.getName(), e);
            return Result.error(0);
        }
    }




}
