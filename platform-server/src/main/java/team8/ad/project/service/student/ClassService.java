package team8.ad.project.service.student;

import team8.ad.project.entity.dto.ClassListItemDTO;
import team8.ad.project.entity.dto.ListDTO;

public interface ClassService {
    ListDTO<ClassListItemDTO> viewClass();
}
