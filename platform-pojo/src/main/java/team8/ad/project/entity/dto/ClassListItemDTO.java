package team8.ad.project.entity.dto;

import lombok.Data;

@Data
public class ClassListItemDTO {
    private Integer classId;     // 对应表字段 id
    private String  className;   // 对应表字段 name
    private String  description; // 对应表字段 description
}
