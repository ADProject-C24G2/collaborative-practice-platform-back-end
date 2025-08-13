package team8.ad.project.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import team8.ad.project.entity.dto.QsResultDTO;
import team8.ad.project.entity.dto.RecommendResponseDTO;
import team8.ad.project.entity.dto.RecommendationDTO;
import team8.ad.project.entity.dto.RecommendationRequestDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.AnswerRecordDTO;
import team8.ad.project.entity.dto.DashboardDTO;
import team8.ad.project.entity.dto.QsInform;
import team8.ad.project.entity.entity.AnswerRecord;
import team8.ad.project.entity.entity.Question;
import team8.ad.project.entity.entity.StudentRecommendation;
import team8.ad.project.mapper.question.QuestionMapper;
import team8.ad.project.service.student.QuestionService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${ml.recommend-url}")
    private String mlRecommendUrl;

    private static final int PAGE_SIZE = 10; // 每页10个题目
    

    public QsResultDTO<QsInform> viewQuestion(String keyword, String questionName, String grade, String subject, String topic, String category, int page, int questionIndex) {
        QsResultDTO<QsInform> result = new QsResultDTO<>();
        try {
            // 计算偏移量，page 从 1 开始
            int offset = (page - 1) * PAGE_SIZE;
            // 获取分页数据
            List<QsInform> items = questionMapper.viewQuestion(keyword, questionName, grade, subject, topic, category, offset, PAGE_SIZE);
            int totalCount = questionMapper.getTotalCount(keyword, questionName, grade, subject, topic, category);

            // 根据 questionIndex 决定返回数据
            if (questionIndex == -1) {
                result.setItems(items); // 返回整页数据
            } else if (questionIndex >= 0 && questionIndex < items.size()) {
                result.setItems(List.of(items.get(questionIndex))); // 只返回指定题
            } else {
                result.setItems(List.of()); // 无效索引返回空列表
                result.setErrorMessage("无效的 questionIndex，超出当前页范围");
                return result;
            }

            result.setTotalCount(totalCount);
            result.setPage(page);
            result.setPageSize(PAGE_SIZE);
        } catch (Exception e) {
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public SelectQuestionDTO getQuestionById(int id) {
        Question question = questionMapper.selectById(id); // ✅ 改成用实体对象
        if (question == null) return null;

        SelectQuestionDTO dto = new SelectQuestionDTO();
        dto.setId(question.getId());
        dto.setImage(question.getImage());
        dto.setQuestion(question.getQuestion());
        dto.setAnswer(question.getAnswer());

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> parsed = mapper.readValue(
                question.getChoices(), new TypeReference<List<String>>() {}
            );
            dto.setChoices(parsed);
        } catch (Exception e) {
            e.printStackTrace();
            dto.setChoices(List.of());
        }

        return dto;
    }


    public boolean saveAnswerRecord(AnswerRecordDTO dto) {
        long studentId = currentUserIdOrThrow();
        AnswerRecord record = new AnswerRecord();
        record.setStudentId(studentId);
        record.setQuestionId((long) dto.getId());
        record.setIsCorrect(dto.getCorrect());
        record.setAnswer(dto.getParam());
        // timestamp 由数据库自动设置
        return questionMapper.saveAnswerRecord(record) > 0;
    }

    public DashboardDTO getDashboardData() {
        long studentId = currentUserIdOrThrow();
        DashboardDTO dto = new DashboardDTO();
        LocalDate today = LocalDate.now();
        dto.setAccuracyRates(new Double[7]);

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            List<AnswerRecord> records = questionMapper.getRecordsByStudentAndDate(studentId, date);
            long total = records.size();
            long correct = records.stream().filter(r -> r.getIsCorrect() == 1).count();
            double accuracy = (total > 0) ? (double) correct / total : 0.0;
            dto.getAccuracyRates()[i] = accuracy;
        }
        return dto;
    }

    public RecommendationDTO getRecommendData() {
        long studentId = currentUserIdOrThrow();
        List<AnswerRecord> records = questionMapper.getRecordsByStudent(studentId);
        RecommendationDTO dto = new RecommendationDTO();
        dto.setRecords(records.stream()
                .map(r -> {
                    RecommendationDTO.AnswerRecordItem item = new RecommendationDTO.AnswerRecordItem();
                    item.setQuestionId(r.getQuestionId());
                    item.setIsCorrect(r.getIsCorrect());
                    return item;
                })
                .collect(Collectors.toList()));
        
        //  触发 ML 模型调用
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RecommendationDTO> request = new HttpEntity<>(dto, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(mlRecommendUrl, request, String.class);
            log.info("ML服务响应: {}", response.getBody());
            try {
                Map<String, List<Long>> resultMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
                List<Long> questionIds = resultMap.get("questionId");

                if (questionIds != null && !questionIds.isEmpty()) {
                    RecommendationRequestDTO saveDTO = new RecommendationRequestDTO();
                    saveDTO.setQuestionIds(questionIds);
                    saveRecommendedQuestions(saveDTO);
                    log.info("推荐题目已保存: {}", questionIds);
                }
            } catch (Exception e) {
                log.error("解析或保存推荐题目失败: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("调用ML推荐服务失败: {}", e.getMessage(), e);
        }

        return dto;
    }

    public boolean saveRecommendedQuestions(RecommendationRequestDTO dto) {
        long studentId = currentUserIdOrThrow();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonQuestions = objectMapper.writeValueAsString(dto.getQuestionIds());
            StudentRecommendation recommendation = new StudentRecommendation();
            recommendation.setStudentId(studentId);
            recommendation.setRecommendedQuestions(jsonQuestions);

            int count = questionMapper.countRecommendationsByStudentId(studentId);
            int result;
            if (count > 0) {
                // 更新现有记录
                result = questionMapper.updateRecommendedQuestions(recommendation);
            } else {
                // 插入新记录
                result = questionMapper.saveRecommendedQuestions(recommendation);
            }
            return result > 0;
        } catch (Exception e) {
            log.error("保存推荐题目失败: {}", e.getMessage());
            return false;
        }
    }

    public RecommendResponseDTO getRecommendQuestions() {
        long studentId = currentUserIdOrThrow();
        try {
            StudentRecommendation recommendation = questionMapper.getRecommendationByStudentId(studentId);
            RecommendResponseDTO dto = new RecommendResponseDTO();
            if (recommendation != null && recommendation.getRecommendedQuestions() != null) {
                dto.setQuestionIds(objectMapper.readValue(recommendation.getRecommendedQuestions(), new TypeReference<List<Long>>(){}));
            } else {
                dto.setQuestionIds(Collections.emptyList());
            }
            return dto;
        } catch (Exception e) {
            log.error("获取推荐题目失败: {}", e.getMessage(), e);
            return null;
        }
    }

    private long currentUserIdOrThrow() {
        Integer id = BaseContext.getCurrentId();
        if (id == null || id <= 0) throw new IllegalStateException("未登录");
        return id.longValue();
    }
}