package team8.ad.project.service.student.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import team8.ad.project.entity.dto.*;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.entity.*;
import team8.ad.project.mapper.question.QuestionMapper;
import team8.ad.project.mapper.teacher.ClassMapper;
import team8.ad.project.result.Result;
import team8.ad.project.service.student.QuestionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    //注册用别的不用
    @Autowired
    private ClassMapper classMapper;

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

    // public RecommendationDTO getRecommendData(Long studentId) {
    //     if (studentId == null) {
    //         studentId = currentUserIdOrThrow();
    //     }

    //     List<AnswerRecord> records = questionMapper.getRecordsByStudent(studentId);
    //     RecommendationDTO dto = new RecommendationDTO();
    //     dto.setRecords(records.stream()
    //             .map(r -> {
    //                 RecommendationDTO.AnswerRecordItem item = new RecommendationDTO.AnswerRecordItem();
    //                 item.setQuestionId(r.getQuestionId());
    //                 item.setIsCorrect(r.getIsCorrect());
    //                 return item;
    //             })
    //             .collect(Collectors.toList()));

    //     try {
    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);

    //         // 关键：把 studentId 一起发给 Python
    //         Map<String, Object> payload = Map.of(
    //             "studentId", studentId,
    //             "records", dto.getRecords()
    //         );

    //         HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
    //         ResponseEntity<String> response = restTemplate.postForEntity(mlRecommendUrl, request, String.class);
    //         log.info("ML服务响应: {}", response.getBody());

    //         Map<String, List<Long>> resultMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
    //         List<Long> questionIds = resultMap.get("questionId");

    //         if (questionIds != null && !questionIds.isEmpty()) {
    //             RecommendationRequestDTO saveDTO = new RecommendationRequestDTO();
    //             saveDTO.setQuestionIds(questionIds);
    //             saveDTO.setStudentId(studentId);
    //             saveRecommendedQuestions(saveDTO);
    //             log.info("推荐题目已保存: {}", questionIds);
    //         }
    //     } catch (Exception e) {
    //         log.error("调用ML推荐服务失败: {}", e.getMessage(), e);
    //     }

    //     return dto;
    // }
    @Override
    public RecommendationDTO getRecommendData(Long studentId) {
        // 这里的 studentId 来自控制器，已确保不为 null
        List<AnswerRecord> records = questionMapper.getRecordsByStudent(studentId);

        RecommendationDTO dto = new RecommendationDTO();
        dto.setStudentId(studentId);  
        dto.setRecords(records.stream().map(r -> {
            RecommendationDTO.AnswerRecordItem item = new RecommendationDTO.AnswerRecordItem();
            item.setQuestionId(r.getQuestionId());
            item.setIsCorrect(r.getIsCorrect());
            return item;
        }).collect(Collectors.toList()));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 约定：给 ML 的 payload 必含 studentId
            Map<String, Object> payload = Map.of(
                "studentId", studentId,
                "records", dto.getRecords()
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(mlRecommendUrl, request, String.class);
            log.info("ML服务响应: {}", resp.getBody());

            // 如果 ML 直接回了题目（同步），你也可以在这里直接保存：
            // Map<String, List<Long>> map = objectMapper.readValue(resp.getBody(), new TypeReference<>() {});
            // List<Long> qids = map.get("questionId");
            // if (qids != null && !qids.isEmpty()) {
            //     RecommendationRequestDTO saveDTO = new RecommendationRequestDTO();
            //     saveDTO.setStudentId(studentId);
            //     saveDTO.setQuestionIds(qids);
            //     saveRecommendedQuestions(saveDTO);
            // }

        } catch (Exception e) {
            log.error("调用ML推荐服务失败: {}", e.getMessage(), e);
        }
        return dto;
    }


    @Override
    public boolean saveRecommendedQuestions(RecommendationRequestDTO dto) {
        // 1) 优先用 body 里的 studentId
        Long studentId = dto.getStudentId();

        // 2) 没有的话，再尝试 Session（BaseContext）
        if (studentId == null) {
            Integer sid = BaseContext.getCurrentId();
            if (sid != null && sid > 0) {
                studentId = sid.longValue();
            }
        }

        // 3) 还没有 -> 明确抛业务错误（不要去调 currentUserIdOrThrow）
        if (studentId == null) {
            log.warn("saveRecommendedQuestions: no studentId in body and no login session");
            // 你也可以 return false; 看你统一的 Result 约定
            throw new IllegalStateException("缺少 studentId");
        }

        try {
            String jsonQuestions = objectMapper.writeValueAsString(dto.getQuestionIds());
            StudentRecommendation recommendation = new StudentRecommendation();
            recommendation.setStudentId(studentId);
            recommendation.setRecommendedQuestions(jsonQuestions);

            int count = questionMapper.countRecommendationsByStudentId(studentId);
            int result = (count > 0)
                    ? questionMapper.updateRecommendedQuestions(recommendation)
                    : questionMapper.saveRecommendedQuestions(recommendation);

            return result > 0;
        } catch (Exception e) {
            log.error("保存推荐题目失败: {}", e.getMessage(), e);
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


    /**
     * Register
     * @param registerDTO
     */
    @Override
    @Transactional
    public String register(RegisterDTO registerDTO) {
        // 检查邮箱是否已存在
        User existingUser = classMapper.findByEmail(registerDTO.getEmail());
        if (existingUser != null) {
            // [!code focus] 2. 如果邮箱重复，返回错误信息字符串
            return "Registration failed: The email address is already in use.";
        }

        // 邮箱可用，继续执行注册流程...
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);

        user.setPassword(registerDTO.getPassword());

        user.setAvatar("https://img.freepik.com/premium-vector/female-teacher-cute-woman-stands-with-pointer-book-school-learning-concept-teacher-s-day_335402-428.jpg");
        user.setUserType("student");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        classMapper.insertUser(user);
        log.info("New teacher registered with ID: {}", user.getId());

        List<String> tagLabels = registerDTO.getTags();
        if (!CollectionUtils.isEmpty(tagLabels)) {
            List<Tag> tags = tagLabels.stream()
                    .map(label -> new Tag(user.getId(), label))
                    .collect(Collectors.toList());

            classMapper.insertTags(tags);
            log.info("Inserted {} tags for teacher ID: {}", tags.size(), user.getId());
        }

        // [!code focus] 3. 如果所有操作都成功，返回 null
        return null;
    }

    @Override
    public void uploadQuestion(QuestionDTO questionDTO) {
        Question questionEntity = new Question();
        BeanUtils.copyProperties(questionDTO, questionEntity, "image");

        // Base64解码逻辑 (保持不变)
        String base64Image = questionDTO.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                String pureBase64 = base64Image.substring(base64Image.indexOf(",") + 1);
                byte[] imageBytes = java.util.Base64.getDecoder().decode(pureBase64);
                questionEntity.setImage(imageBytes);
            } catch (Exception e) {
                log.error("Failed to decode Base64 image string.", e);
                throw new RuntimeException("Invalid Base64 image format.", e);
            }
        }

        // [!code focus:3] 3. 使用Fastjson进行序列化
        // Fastjson的toJSONString方法不会抛出受检异常，代码更简洁
        String choicesAsJson = JSON.toJSONString(questionDTO.getOptions());
        questionEntity.setChoices(choicesAsJson);

        classMapper.insertQuestion(questionEntity);
        log.info("Successfully inserted question with id: {}", questionEntity.getId());

    }
}