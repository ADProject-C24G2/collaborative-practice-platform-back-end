package team8.ad.project.entity.dto;

import lombok.Data;

@Data
public class SubmissionDetailDTO {
    private String studentId;
    private String studentName; // From the students table
    private int whetherFinish;
    private String finishTime;
    private String accuracy; // Mapped from the 'Accurancy' DB column
}
