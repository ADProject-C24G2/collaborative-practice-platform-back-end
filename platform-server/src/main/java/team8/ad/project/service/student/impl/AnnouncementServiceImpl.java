package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.AnnouncementItemDTO;
import team8.ad.project.entity.dto.AnnouncementListDTO;
import team8.ad.project.mapper.student.AnnouncementMapper;
import team8.ad.project.service.student.AnnouncementService;

import java.util.Collections;
import java.util.List;

@Service("studentAnnouncementService")
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    @Qualifier("studentAnnouncementMapper")
    private AnnouncementMapper announcementMapper;

    @Override
    public AnnouncementListDTO selectAnnouncement(Integer classId) {
        AnnouncementListDTO dto = new AnnouncementListDTO();
        try {
            Long currentId = (long)BaseContext.getCurrentId();
            if (currentId == null || currentId == 0) {
                BaseContext.setCurrentId(1); // 设置默认 ID
            }
            List<AnnouncementItemDTO> list = announcementMapper.listByClassAndStudent(classId, currentId);
            dto.setList(list == null ? Collections.emptyList() : list);
        } catch (Exception e) {
            log.error("查询公告失败, classId={}, err={}", classId, e.getMessage(), e);
            dto.setList(Collections.emptyList());
        } finally {
            BaseContext.removeCurrentId();
        }
        return dto;
    }
}
