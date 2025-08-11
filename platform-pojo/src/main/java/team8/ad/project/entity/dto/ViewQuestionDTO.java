package team8.ad.project.entity.dto;


import lombok.Data;

import java.util.List;

@Data
public class ViewQuestionDTO {

    private String keyword;

    private String questionName;

    private List<String> grade;

    private List<String>  subject;

    private List<String>  topic;

    private List<String>  category;

    private int page;

    private int count;
}
