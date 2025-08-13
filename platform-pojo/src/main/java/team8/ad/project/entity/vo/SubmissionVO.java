package team8.ad.project.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SubmissionVO {
    // key字段名与前端一致，无需注解
    private String key;

    @JSONField(name = "student_id") // [!code focus] 2. 替换注解
    private String studentId;

    @JSONField(name = "student_name") // [!code focus] 2. 替换注解
    private String studentName;

    @JSONField(name = "whether_finish") // [!code focus] 2. 替换注解
    private int whetherFinish;

    // accuracy字段名与前端一致，无需注解
    private String accuracy;

    @JSONField(name = "finish_time") // [!code focus] 2. 替换注解
    private String finishTime;
}
