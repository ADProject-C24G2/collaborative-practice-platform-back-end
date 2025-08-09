package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class QsResultDTO<T> {
    private List<T> items;
    private int totalCount; // 代表总体数
    private int page;
    private int pageSize; // 默认一页最多10题
    private String errorMessage;
}