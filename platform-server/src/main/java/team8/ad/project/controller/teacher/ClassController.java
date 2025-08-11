package team8.ad.project.controller.teacher;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.AnnouncementDTO;
import team8.ad.project.entity.dto.ViewQuestionDTO;
import team8.ad.project.entity.entity.Announcement;
import team8.ad.project.entity.vo.*;
import team8.ad.project.entity.dto.ClassDTO;
import team8.ad.project.result.Result;
import team8.ad.project.service.teacher.ClassService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher/class")
@Api(tags = "Class-related interfaces")
@Slf4j
public class ClassController {

    @Autowired
    private ClassService classService;



    /**
     * Create a new class
     * @param classDTO
     * @return
     */
    @PostMapping("/create")
    @ApiOperation("Create Class")
    public Result createClass(@RequestBody ClassDTO classDTO){

        log.info("Create Class:{}", classDTO);
        // TODO Here need set the teacherId
        BaseContext.setCurrentId(1);

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
    public Result getTeacherProfile(){
        log.info("Get teacher profile");
        // TODO 假设登陆的老师id = 1
        BaseContext.setCurrentId(1);
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
    public Result getClassList(@RequestParam(defaultValue = "30") int count) {
        log.info("Get class list, requested count: {}", count);
        try {
            // TODO 模拟id
            // 1. 获取当前登录教师的ID (假设已通过认证设置到 BaseContext)
            BaseContext.setCurrentId(1);
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
    public Result getStudent(@RequestParam(defaultValue = "30") int classId) {
        log.info("start to get students");
        // TODO 模拟id
        BaseContext.setCurrentId(1);
        int currentTeacherId = BaseContext.getCurrentId();
        List<StudentVO> studentsVO = classService.getStudents(classId);
        return Result.success(studentsVO);
    }


    @PostMapping("/make-announcement")
    @ApiOperation("make announcement")
    public Result makeAnnouncement(@RequestBody AnnouncementDTO announcementDTO) {
        log.info("start to make announcement");
        // TODO 模拟id
        BaseContext.setCurrentId(1);
        classService.inserAnnouncement(announcementDTO);
        return Result.success();
    }

    /**
     * get the class detials like students profiles and announcement
     * @param classId
     * @return
     */
    @GetMapping("/get-announcement")
    @ApiOperation("view the class details and manage")
    public Result manageClass(@RequestParam int classId) {
        log.info("start to manage class");
        // TODO 模拟id
        BaseContext.setCurrentId(1);
        List<AnnouncementVO> announcementVO = classService.getAnnouncement(classId);
        return Result.success(announcementVO);

    }


    /**
     * make assignment
     * @param assignment
     * @return
     */
    @PostMapping("/assign-assignment")
    @ApiOperation("make assignment")
    public Result assignAssignment(@RequestBody AllArguments.Assignment assignment) {
        log.info("start to assign assignment");
        return Result.success();
    }

    /**
     * Get questions
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
     * @param assignment
     * @return
     */
    @PostMapping("/make-assignment")
    @ApiOperation("make-assignment form")
    public Result makeAssignment(@RequestBody AllArguments.Assignment assignment) {

        return Result.success();
    }



}
