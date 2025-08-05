package team8.ad.project.service.student.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.ad.project.entity.dto.QsResultDTO;
import team8.ad.project.entity.dto.SelectQuestionDTO;
import team8.ad.project.entity.dto.AnswerRecordDTO;
import team8.ad.project.entity.dto.DashboardDTO;
import team8.ad.project.entity.dto.QsInform;
import team8.ad.project.entity.entity.AnswerRecord;
import team8.ad.project.mapper.question.QuestionMapper;
import team8.ad.project.service.student.QuestionService;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    private static final int PAGE_SIZE = 10; // 每页10个题目
    private static final Long DEFAULT_STUDENT_ID = 1L;

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

    // TODO 后续将DEFAULT_STUDENT_ID换成session获取的当前用户id
    public boolean saveAnswerRecord(AnswerRecordDTO dto) {
        AnswerRecord record = new AnswerRecord();
        record.setStudentId(DEFAULT_STUDENT_ID); // 使用默认值
        record.setQuestionId((long) dto.getId());
        record.setIsCorrect(dto.getCorrect());
        record.setAnswer(dto.getParam());
        // timestamp 由数据库自动设置
        return questionMapper.saveAnswerRecord(record) > 0;
    }

    public DashboardDTO getDashboardData() {
        DashboardDTO dto = new DashboardDTO();
        LocalDate today = LocalDate.now();
        dto.setAccuracyRates(new Double[7]);

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            List<AnswerRecord> records = questionMapper.getRecordsByStudentAndDate(DEFAULT_STUDENT_ID, date);
            long total = records.size();
            long correct = records.stream().filter(r -> r.getIsCorrect() == 1).count();
            double accuracy = (total > 0) ? (double) correct / total : 0.0;
            dto.getAccuracyRates()[i] = accuracy;
        }
        return dto;
    }
}