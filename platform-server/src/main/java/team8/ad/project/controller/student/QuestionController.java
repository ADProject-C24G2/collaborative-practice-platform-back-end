package team8.ad.project.controller.student;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.*;
import team8.ad.project.entity.vo.LoginResultVO;
import team8.ad.project.result.Result;
import team8.ad.project.service.student.QuestionService;
import team8.ad.project.service.student.AnnouncementService;
import team8.ad.project.service.student.AssignmentService;
import team8.ad.project.service.student.ClassService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/student")
@Api(tags = "题目相关接口")
@Slf4j
public class QuestionController {

    @Autowired
    @Qualifier("studentClassServiceImpl")
    private ClassService classServiceImpl;

    @Autowired
    private QuestionService questionServiceImpl;

    @Autowired
    @Qualifier("studentAssignmentService")
    private AssignmentService assignmentService;

    @Autowired
    @Qualifier("studentAnnouncementService")
    private AnnouncementService announcementService;


    @GetMapping("/viewQuestion")
    @ApiOperation("查看题目（支持关键词和题目名称，带分页，可选指定第几题）")
    public Result<QsResultDTO<QsInform>> viewQuestion(
            @ApiParam(value = "搜索关键词", required = false) @RequestParam(required = false, defaultValue = "") String keyword,
            @ApiParam(value = "题目名称", required = false) @RequestParam(required = false, defaultValue = "") String questionName,
            @ApiParam(value = "年级", required = false) @RequestParam(required = false, defaultValue = "") String grade,
            @ApiParam(value = "学科", required = false) @RequestParam(required = false, defaultValue = "") String subject,
            @ApiParam(value = "主题", required = false) @RequestParam(required = false, defaultValue = "") String topic,
            @ApiParam(value = "分类", required = false) @RequestParam(required = false, defaultValue = "") String category,
            @ApiParam(value = "页码", required = true, defaultValue = "1") @RequestParam int page,
            @ApiParam(value = "当前页第几题(从0开始,非必要，-1表示返回整页)", required = false, defaultValue = "-1") @RequestParam(defaultValue = "-1") int questionIndex) {
        log.info("查看题目: keyword={}, questionName={}, grade={}, subject={}, topic={}, category={}, page={}, questionIndex={}", 
                keyword, questionName, grade, subject, topic, category, page, questionIndex);
        
        QsResultDTO<QsInform> dto = questionServiceImpl.viewQuestion(keyword, questionName, grade, subject, topic, category, page, questionIndex);
        if (dto.getErrorMessage() != null) {
            return Result.error(dto.getErrorMessage());
        }
        return Result.success(dto);
    }

    @GetMapping("/doquestion")
    @ApiOperation("查看具体题目并做题")
    public Result<SelectQuestionDTO> selectQuestion(@ApiParam(value = "题目序号", required = true, defaultValue = "1") @RequestParam int id) {
        log.info("查看具体题目: id={}", id);
        SelectQuestionDTO dto = questionServiceImpl.getQuestionById(id);
        if (dto == null) {
            return Result.error("题目不存在");
        }
        return Result.success(dto);
    }

    @GetMapping("/answerQuestion")
    @ApiOperation("提交答题结果")
    public Result<String> answerQuestion(
            @ApiParam(value = "题目ID", required = true, defaultValue = "1") @RequestParam Integer id,
            @ApiParam(value = "正确性(0错,1对)", required = true, defaultValue = "0") @RequestParam Integer correct,
            @ApiParam(value = "答案", required = false) @RequestParam(required = false) Integer param) {
        log.info("提交答题: id={}, correct={}, param={}", id, correct, param);
        if (id == null || correct == null) {
            return Result.error("题目ID和正确性不能为空");
        }
        if (correct != 0 && correct != 1) {
            return Result.error("correct 值必须为 0 或 1");
        }
        AnswerRecordDTO dto = new AnswerRecordDTO();
        dto.setId(id);
        dto.setCorrect(correct);
        dto.setParam(param);
        boolean success = questionServiceImpl.saveAnswerRecord(dto);
        return success ? Result.success("答题记录保存成功") : Result.error("答题记录保存失败");
    }

    @GetMapping("/dashboard")
    @ApiOperation("获取过去7天的做题准确率")
    public Result<DashboardDTO> getDashboard() {
        log.info("获取仪表盘数据");
        DashboardDTO dto = questionServiceImpl.getDashboardData();
        return dto != null ? Result.success(dto) : Result.error("无法获取仪表盘数据");
    }

    // 通过全局配置url:recommend-url:来与model建立联系，需要修改application-dev.xml中的recommend-url来与ml中的url对应
    // @PutMapping("/recommend")
    // @ApiOperation("触发模型，并向模型提供训练样本(题目)")
    // public Result<RecommendationDTO> getRecommend(
    //         @RequestBody(required = false) Map<String, Object> body,
    //         HttpSession session) {

    //     log.info("获取推荐数据");
    //     Long studentId = null;

    //     // 1. 优先从 session 获取
    //     Object sessionId = session.getAttribute("studentId");
    //     if (sessionId instanceof Integer) {
    //         studentId = ((Integer) sessionId).longValue();
    //     } else if (sessionId instanceof Long) {
    //         studentId = (Long) sessionId;
    //     }

    //     // 2. 如果 session 没有，再从 body 获取
    //     if (studentId == null && body != null) {
    //         Object bodyId = body.get("studentId");
    //         if (bodyId instanceof Integer) {
    //             studentId = ((Integer) bodyId).longValue();
    //         } else if (bodyId instanceof Long) {
    //             studentId = (Long) bodyId;
    //         } else if (bodyId instanceof String) {
    //             try {
    //                 studentId = Long.parseLong((String) bodyId);
    //             } catch (NumberFormatException ignored) {}
    //         }
    //     }

    //     if (studentId == null) {
    //         return Result.error("缺少 studentId，请先登录或在请求中传入 studentId");
    //     }

    //     RecommendationDTO dto = questionServiceImpl.getRecommendData(studentId);
    //     return dto != null ? Result.success(dto) : Result.error("无法提供推荐数据");
    // }
    @PutMapping("/recommend")
    public Result<RecommendationDTO> getRecommend() {
        Long sid = Optional.ofNullable(BaseContext.getCurrentId())
                .map(Integer::longValue).orElse(null);
        if (sid == null) {
            return Result.error("请先登录");
        }
        RecommendationDTO dto = questionServiceImpl.getRecommendData(sid);
        return Result.success(dto);
    }


    // @PostMapping("/recommendQuestion")
    // @ApiOperation("接收推荐题目并存储")
    // public Result<String> recommendQuestion(@RequestBody RecommendationRequestDTO dto) {
    //     log.info("接收推荐题目: questionIds={}", dto.getQuestionIds());
    //     if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
    //         return Result.error("推荐题目列表不能为空");
    //     }
    //     boolean success = questionServiceImpl.saveRecommendedQuestions(dto);
    //     return success ? Result.success("推荐题目保存成功") : Result.error("推荐题目保存失败");
    // }

    @PostMapping("/recommendQuestion")
    @ApiOperation("接收推荐题目并存储")
    public Result<String> recommendQuestion(@RequestBody RecommendationRequestDTO dto) {
        log.info("接收推荐题目: studentId={}, questionIds={}", dto.getStudentId(), dto.getQuestionIds());
        if (dto.getStudentId() == null) {
            return Result.error("缺少 studentId");
        }
        if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
            return Result.error("推荐题目列表不能为空");
        }
        boolean ok = questionServiceImpl.saveRecommendedQuestions(dto);
        return ok ? Result.success("推荐题目保存成功") : Result.error("推荐题目保存失败");
    }

    @GetMapping("/getRecommend")
    @ApiOperation("获取当前用户所有推荐题目ID")
    public Result<RecommendResponseDTO> getRecommendQuestions() {
        log.info("获取推荐题目ID");
        RecommendResponseDTO dto = questionServiceImpl.getRecommendQuestions();
        return dto != null ? Result.success(dto) : Result.error("无法获取推荐题目ID");
    }

    @PostMapping("/joinClass")
    @ApiOperation("加入班级(byLink 用 token；byName 用 name)")
    public Result<String> joinClass(
            @ApiParam(value = "加入方式: byLink/byName", required = true) @RequestParam String accessType,
            @ApiParam(value = "凭据: byLink=token, byName=班级名称", required = true) @RequestParam String key) {

        log.info("加入班级请求: accessType={}, key={}", accessType, key);
        String err = classServiceImpl.joinClass(accessType, key);
        if (err == null) {
            return Result.success("加入成功");
        }
        return Result.error(err);
    }

    @PostMapping("/leaveClass")
    @ApiOperation("离开班级")
    public Result<String> leaveClass(
            @ApiParam(value = "班级ID", required = true) @RequestParam Integer classId) {
        log.info("离开班级: classId={}", classId);
        String err = classServiceImpl.leaveClass(classId);
        return err == null ? Result.success("已离开该班级") : Result.error(err);
    }

    @GetMapping("/viewClass")
    @ApiOperation("查看所有课程")
    public Result<team8.ad.project.entity.dto.ListDTO<team8.ad.project.entity.dto.ClassListItemDTO>> viewClass() {
        log.info("查看课程列表");
        var dto = classServiceImpl.viewClass();
        return Result.success(dto);
    }

    @GetMapping("/selectClass")
    @ApiOperation("根据classId查看作业列表")
    public Result<AssignmentListRespDTO> viewAssignment(@ApiParam(value = "班级ID", required = true) @RequestParam Integer classId) {
        if (classId == null) {
            return Result.error("classId不能为空");
        }
        log.info("查看作业列表: classId={}", classId);
        AssignmentListRespDTO dto = assignmentService.viewByClassId(classId);
        return Result.success(dto);
    }

    @GetMapping("/selectAssignment")
    @ApiOperation("根据assignmentId获取题目列表")
    public Result<team8.ad.project.entity.dto.ListDTO<SelectQuestionDTO>> selectAssignment(
            @ApiParam(value = "作业ID", required = true) @RequestParam Integer assignmentId) {
        if (assignmentId == null) {
            return Result.error("assignmentId不能为空");
        }
        log.info("按作业获取题目: assignmentId={}", assignmentId);
        var dto = assignmentService.selectQuestionsByAssignmentId(assignmentId);
        return Result.success(dto);
    }

    @GetMapping("/submitAssignment")
    @ApiOperation("提交作业:根据assignmentId返回题目列表(id/question/choices/image)")
    public Result<team8.ad.project.entity.dto.ListDTO<team8.ad.project.entity.dto.SubmitQuestionDTO>> submitAssignment(
            @ApiParam(value = "作业ID", required = true) @RequestParam Integer assignmentId) {
        if (assignmentId == null) {
            return Result.error("assignmentId不能为空");
        }
        log.info("Submit Assignment: assignmentId={}", assignmentId);
        var dto = assignmentService.submitAssignment(assignmentId);
        return Result.success(dto);
    }

    @GetMapping("/selectAnnouncement")
    @ApiOperation("根据classId获取当前用户相关公告列表")
    public Result<AnnouncementListDTO> selectAnnouncement(
            @ApiParam(value = "班级ID", required = true) @RequestParam Integer classId) {
        if (classId == null) {
            return Result.error("classId不能为空");
        }
        log.info("查询公告: classId={}", classId);
        AnnouncementListDTO dto = announcementService.selectAnnouncement(classId);
        return Result.success(dto);
    }

    @PostMapping("/checkAnnouncement")
    @ApiOperation("将公告标记为已读（status=1）")
    public Result<String> checkAnnouncement(
            @ApiParam(value = "公告ID", required = true)
            @RequestParam Integer announcementId) {

        log.info("标记公告已读: announcementId={}", announcementId);
        String err = announcementService.checkAnnouncement(announcementId);
        return err == null ? Result.success("已标记为已读") : Result.error(err);
    }

    @PostMapping("/finishAssignment")
    @ApiOperation("上报/更新作业完成状态（支持幂等）")
    public Result<String> finishAssignment(
            @ApiParam(value = "作业ID", required = true) @RequestParam Integer assignmentId,
            @ApiParam(value = "是否完成(0未完成,1已完成)", required = true) @RequestParam Integer whether,
            @ApiParam(value = "准确率(百分制，如85.50)", required = true) @RequestParam BigDecimal accuracy) {

        team8.ad.project.entity.dto.AssignmentProgressDTO dto = new team8.ad.project.entity.dto.AssignmentProgressDTO();
        dto.setAssignmentId(assignmentId);
        dto.setWhetherFinish(whether);
        dto.setAccuracy(accuracy);

        log.info("上报作业完成状态: assignmentId={}, whetherFinish={}, accuracy={}", assignmentId, whether, accuracy);
        String err = assignmentService.saveOrUpdateAssignmentProgress(dto);
        return err == null ? Result.success("已保存") : Result.error(err);
    }




    /**
     * 用户登录
     * @param loginDTO
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("学生用户登录")
    public LoginResultVO login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return classServiceImpl.login(loginDTO, session);
    }

    /**
     * Register
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("Student Register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("Processing registration for user: {}", registerDTO.getName());
        try {
            // [!code focus:start]
            // 1. 调用service并接收返回值
            String errorMessage = questionServiceImpl.register(registerDTO);

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

    /**
     * upload question
     * @param quesionDTO
     * @return
     */
    @PostMapping("/upload-question")
    @ApiOperation("upload question")
    public Result uploadQuestion(@RequestBody QuestionDTO quesionDTO) {
        questionServiceImpl.uploadQuestion(quesionDTO);
        return Result.success("The question has been successfully uploaded.");
    }

}