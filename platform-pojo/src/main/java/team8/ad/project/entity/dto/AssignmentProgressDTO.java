package team8.ad.project.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssignmentProgressDTO {

    private Integer assignmentId;
    private Integer whetherFinish;
    private BigDecimal accuracy; // 对应表字段 Accurancy
}
