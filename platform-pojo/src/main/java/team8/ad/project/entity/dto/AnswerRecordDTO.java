package team8.ad.project.entity.dto;

import lombok.Data;

@Data
public class AnswerRecordDTO {

    private Integer id; // 题目 ID

    private Integer correct; // 0 表示错，1 表示对

    private Integer param; // 学生提交的答案
}