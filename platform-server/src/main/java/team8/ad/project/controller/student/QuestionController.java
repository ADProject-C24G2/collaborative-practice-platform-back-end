package team8.ad.project.controller.student;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import team8.ad.project.entity.dto.AnnouncementListDTO;
import team8.ad.project.entity.dto.AnswerRecordDTO;
import team8.ad.project.entity.dto.AssignmentListRespDTO;
import team8.ad.project.entity.dto.DashboardDTO;
import team8.ad.project.entity.dto.QsInform;
import team8.ad.project.entity.dto.QsResultDTO;
import team8.ad.project.entity.dto.RecommendResponseDTO;
import team8.ad.project.entity.dto.RecommendationDTO;
import team8.ad.project.entity.dto.RecommendationRequestDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.result.Result;
import team8.ad.project.service.student.QuestionService;
import team8.ad.project.service.student.impl.QuestionServiceImpl;
import team8.ad.project.service.student.AnnouncementService;
import team8.ad.project.service.student.AssignmentService;
import team8.ad.project.service.student.ClassService;

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
    @PutMapping("/recommend")
    @ApiOperation("触发模型，并向模型提供训练样本(题目)")
    public Result<RecommendationDTO> getRecommend() {
        log.info("获取推荐数据");
        RecommendationDTO dto = questionServiceImpl.getRecommendData();
        return dto != null ? Result.success(dto) : Result.error("无法提供推荐数据");
    }

    @PostMapping("/recommendQuestion")
    @ApiOperation("接收推荐题目并存储")
    public Result<String> recommendQuestion(@RequestBody RecommendationRequestDTO dto) {
        log.info("接收推荐题目: questionIds={}", dto.getQuestionIds());
        if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
            return Result.error("推荐题目列表不能为空");
        }
        boolean success = questionServiceImpl.saveRecommendedQuestions(dto);
        return success ? Result.success("推荐题目保存成功") : Result.error("推荐题目保存失败");
    }

    @GetMapping("/getRecommend")
    @ApiOperation("获取当前用户所有推荐题目ID")
    public Result<RecommendResponseDTO> getRecommendQuestions() {
        log.info("获取推荐题目ID");
        RecommendResponseDTO dto = questionServiceImpl.getRecommendQuestions();
        return dto != null ? Result.success(dto) : Result.error("无法获取推荐题目ID");
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
}