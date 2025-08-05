package team8.ad.project.service.student;

import team8.ad.project.entity.dto.*;

public interface QuestionService {
    public QsResultDTO<QsInform> viewQuestion(String keyword, String questionName, String grade, String subject, String topic, String category, int page, int questionIndex);
    public boolean saveAnswerRecord(AnswerRecordDTO dto);
    public DashboardDTO getDashboardData();
    public SelectQuestionDTO getQuestionById(int id);
}
