package team8.ad.project.entity.dto;

import lombok.Data;
import java.util.Collections;
import java.util.List;

@Data
public class DeleteAssignmentDTO {

    /** 可选：班级ID（如果删除要校验所属班级的话） */
    private Integer classId;

    /** 单个删除时用 */
    private Integer assignmentId;

    /** 批量删除时用 */
    private List<Integer> assignmentIds;

    /** 方便控制层统一拿到ID列表 */
    public List<Integer> safeIds() {
        if (assignmentIds != null && !assignmentIds.isEmpty()) {
            return assignmentIds;
        }
        return assignmentId == null ? Collections.emptyList() : Collections.singletonList(assignmentId);
    }
}
