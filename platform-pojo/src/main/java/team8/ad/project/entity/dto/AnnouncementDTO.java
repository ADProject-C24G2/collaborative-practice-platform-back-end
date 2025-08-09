package team8.ad.project.entity.dto;


import lombok.Data;

import java.util.List;

@Data
public class AnnouncementDTO {
    private String title;
    private String recipientType;
    private String content;
    private List<Integer> specificRecipients;
    private String classId;
}
