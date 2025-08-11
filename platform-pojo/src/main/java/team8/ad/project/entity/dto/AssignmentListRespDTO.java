package team8.ad.project.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssignmentListRespDTO {
    private Integer classId;
    private String  className;
    private List<AssignmentItemDTO> list;
}