package team8.ad.project.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
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
import team8.ad.project.entity.entity.StudentRecommendation;
import team8.ad.project.mapper.question.QuestionMapper;
import team8.ad.project.service.student.QuestionService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ObjectMapper objectMapper;

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

    public SelectQuestionDTO getQuestionById(int id) {
        return questionMapper.getQuestionById(id);
    }

    public boolean saveAnswerRecord(AnswerRecordDTO dto) {
        Integer currentId = BaseContext.getCurrentId();
        if (currentId == null || currentId == 0) {
            BaseContext.setCurrentId(1); // 设置默认 ID
        }
        AnswerRecord record = new AnswerRecord();
        record.setStudentId((long) BaseContext.getCurrentId());
        record.setQuestionId((long) dto.getId());
        record.setIsCorrect(dto.getCorrect());
        record.setAnswer(dto.getParam());
        // timestamp 由数据库自动设置
        BaseContext.removeCurrentId();
        return questionMapper.saveAnswerRecord(record) > 0;
    }

    public DashboardDTO getDashboardData() {
        Integer currentId = BaseContext.getCurrentId();
        if (currentId == null || currentId == 0) {
            BaseContext.setCurrentId(1); // 设置默认 ID
        }
        DashboardDTO dto = new DashboardDTO();
        LocalDate today = LocalDate.now();
        dto.setAccuracyRates(new Double[7]);

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            List<AnswerRecord> records = questionMapper.getRecordsByStudentAndDate((long) BaseContext.getCurrentId(), date);
            long total = records.size();
            long correct = records.stream().filter(r -> r.getIsCorrect() == 1).count();
            double accuracy = (total > 0) ? (double) correct / total : 0.0;
            dto.getAccuracyRates()[i] = accuracy;
        }
        BaseContext.removeCurrentId();
        return dto;
    }

    public RecommendationDTO getRecommendData() {
        Integer currentId = BaseContext.getCurrentId();
        if (currentId == null || currentId == 0) {
            BaseContext.setCurrentId(1); // 设置默认 ID
        }
        List<AnswerRecord> records = questionMapper.getRecordsByStudent((long) BaseContext.getCurrentId());
        RecommendationDTO dto = new RecommendationDTO();
        dto.setRecords(records.stream()
                .map(r -> {
                    RecommendationDTO.AnswerRecordItem item = new RecommendationDTO.AnswerRecordItem();
                    item.setQuestionId(r.getQuestionId());
                    item.setIsCorrect(r.getIsCorrect());
                    return item;
                })
                .collect(Collectors.toList()));
        BaseContext.removeCurrentId();
        return dto;
    }

    public boolean saveRecommendedQuestions(RecommendationRequestDTO dto) {
        Integer currentId = BaseContext.getCurrentId();
        if (currentId == null || currentId == 0) {
            BaseContext.setCurrentId(1); // 设置默认 ID
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonQuestions = objectMapper.writeValueAsString(dto.getQuestionIds());
            StudentRecommendation recommendation = new StudentRecommendation();
            recommendation.setStudentId((long) BaseContext.getCurrentId());
            recommendation.setRecommendedQuestions(jsonQuestions);

            int count = questionMapper.countRecommendationsByStudentId((long) BaseContext.getCurrentId());
            int result;
            if (count > 0) {
                // 更新现有记录
                result = questionMapper.updateRecommendedQuestions(recommendation);
            } else {
                // 插入新记录
                result = questionMapper.saveRecommendedQuestions(recommendation);
            }
            BaseContext.removeCurrentId();
            return result > 0;
        } catch (Exception e) {
            log.error("保存推荐题目失败: {}", e.getMessage());
            BaseContext.removeCurrentId();
            return false;
        }
    }

    public RecommendResponseDTO getRecommendQuestions() {
        Integer currentId = BaseContext.getCurrentId();
        if (currentId == null || currentId == 0) {
            BaseContext.setCurrentId(1);
        }
        try {
            StudentRecommendation recommendation = questionMapper.getRecommendationByStudentId((long) BaseContext.getCurrentId());
            RecommendResponseDTO dto = new RecommendResponseDTO();
            if (recommendation != null && recommendation.getRecommendedQuestions() != null) {
                dto.setQuestionIds(objectMapper.readValue(recommendation.getRecommendedQuestions(), new TypeReference<List<Long>>(){}));
            } else {
                dto.setQuestionIds(Collections.emptyList());
            }
            BaseContext.removeCurrentId();
            return dto;
        } catch (Exception e) {
            log.error("获取推荐题目失败: {}", e.getMessage(), e);
            BaseContext.removeCurrentId();
            return null;
        }
    }
}