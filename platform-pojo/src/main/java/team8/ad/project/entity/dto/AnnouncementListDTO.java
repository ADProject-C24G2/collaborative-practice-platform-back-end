package team8.ad.project.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnnouncementListDTO {
    private List<AnnouncementItemDTO> list;
}
