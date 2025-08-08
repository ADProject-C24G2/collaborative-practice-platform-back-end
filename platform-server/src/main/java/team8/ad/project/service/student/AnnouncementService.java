package team8.ad.project.service.student;

import team8.ad.project.entity.dto.AnnouncementListDTO;

public interface AnnouncementService {
    AnnouncementListDTO selectAnnouncement(Integer classId);
}
