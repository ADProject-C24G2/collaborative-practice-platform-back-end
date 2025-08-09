package team8.ad.project.entity.dto;

import lombok.Data;

@Data
public class DashboardDTO {
    private Double[] accuracyRates; // 7天准确率数组，[0]为最近一天，[6]为第七天
}