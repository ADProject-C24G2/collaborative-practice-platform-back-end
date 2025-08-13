package team8.ad.project.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class AssignmentStatusVO {
    @JSONField(name = "assignment_name") // [!code focus] 2. 替换注解
    private String assignmentName;

    // submissions字段名与前端一致，无需注解
    private List<SubmissionVO> submissions;
}

