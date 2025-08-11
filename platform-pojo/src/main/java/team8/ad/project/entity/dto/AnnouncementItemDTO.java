package team8.ad.project.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementItemDTO {
    private Integer id; 
    private String title;
    private String content;
    private LocalDateTime createTime;
    private Integer status;
}