package team8.ad.project.service.teacher;

import team8.ad.project.entity.dto.ClassDTO;

public interface ClassService {

    /**
     * Create Class
     * @param classDTO
     */
    public String createClass(ClassDTO classDTO);
}
