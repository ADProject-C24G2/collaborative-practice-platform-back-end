package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDTO {
    private Long studentId;          // 可为空：python 端/无人登录时传；有 session 时可不传
    private List<Long> questionIds; // 推荐的题目 ID 列表

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }

}