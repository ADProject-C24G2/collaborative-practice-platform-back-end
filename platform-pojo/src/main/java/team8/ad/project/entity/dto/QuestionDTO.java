package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {

    private String image;

    private String question;

    private List<String> options;

    private String answer;

    private String grade;

    private String subject;

    private String topic;

    private String category;
}
