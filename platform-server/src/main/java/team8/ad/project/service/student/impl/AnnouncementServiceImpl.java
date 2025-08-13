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
            long currentId = currentUserIdOrThrow();
            List<AnnouncementItemDTO> list = announcementMapper.listByClassAndStudent(classId, currentId);
            dto.setList(list == null ? Collections.emptyList() : list);
        } catch (Exception e) {
            log.error("查询公告失败, classId={}, err={}", classId, e.getMessage(), e);
            dto.setList(Collections.emptyList());
        } finally {
        }
        return dto;
    }

    @Override
    public String checkAnnouncement(Integer announcementId) {
        try {
            if (announcementId == null) return "announcementId不能为空";
            int changed = announcementMapper.markRead(announcementId);
            if (changed == 0) {
                return "公告不存在或已删除";
            }
            return null; // 成功
        } catch (Exception e) {
            log.error("标记公告已读失败, id={}, err={}", announcementId, e.getMessage(), e);
            return "标记已读失败";
        }
    }

    private long currentUserIdOrThrow() {
        Integer id = BaseContext.getCurrentId();
        if (id == null || id <= 0) throw new IllegalStateException("未登录");
        return id.longValue();
    }
}
