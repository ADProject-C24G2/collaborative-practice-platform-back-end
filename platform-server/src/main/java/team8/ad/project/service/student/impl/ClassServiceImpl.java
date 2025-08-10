package team8.ad.project.service.student.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonSerializable.Base;

import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.ClassListItemDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.mapper.student.ClassMapper;
import team8.ad.project.service.student.ClassService;

import java.util.Collections;

@Slf4j
@Service("studentClassServiceImpl")
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public ListDTO<ClassListItemDTO> viewClass() {
        ListDTO<ClassListItemDTO> dto = new ListDTO<>();
        try {
            dto.setList(classMapper.viewClass());
        } catch (Exception e) {
            log.error("查询课程失败: {}", e.getMessage(), e);
            dto.setList(Collections.emptyList());
        }
        return dto;
    }

    @Override
    public String joinClass(String accessType, String key) {
        try {
            if (accessType == null || key == null || key.trim().isEmpty()) {
                return "accessType和key不能为空";
            }

            accessType = accessType.trim();
            team8.ad.project.entity.entity.Class cls;

            if ("byLink".equalsIgnoreCase(accessType)) {
                cls = classMapper.selectByToken(key.trim());
            } else if ("byName".equalsIgnoreCase(accessType)) {
                cls = classMapper.selectByName(key.trim());
            } else {
                return "不支持的accessType";
            }

            if (cls == null) {
                return "班级不存在或加入方式不匹配";
            }

            // 时间窗口校验
            var now = java.time.LocalDateTime.now();
            if (cls.getAccessAvailable() != null && now.isBefore(cls.getAccessAvailable())) {
                return "还未到加入时间";
            }
            if (cls.getAccessExpiration() != null && now.isAfter(cls.getAccessExpiration())) {
                return "加入时间已过期";
            }

            // 人数校验
            int current = classMapper.countMembers(cls.getId());
            if (cls.getMaxMembers() > 0 && current >= cls.getMaxMembers()) {
                return "班级人数已满";
            }

            // 当前用户
            Integer currentId = BaseContext.getCurrentId();
            if (currentId == null || currentId == 0) {
                BaseContext.setCurrentId(1); // 设置默认 ID
            }
            Long studentId = (long)BaseContext.getCurrentId();

            // 重复加入校验
            if (classMapper.existsMember(cls.getId(), studentId) > 0) {
                return "你已在该班级";
            }

            // 插入关系
            int rows = classMapper.insertMember(cls.getId(), studentId);
            if (rows <= 0) {
                return "加入失败，请稍后重试";
            }

            return null; // null == 成功
        } catch (Exception e) {
            log.error("加入班级失败: accessType={}, key={}, err={}", accessType, key, e.getMessage(), e);
            return "系统异常";
        } finally {
            team8.ad.project.context.BaseContext.removeCurrentId();
        }
    }
}
