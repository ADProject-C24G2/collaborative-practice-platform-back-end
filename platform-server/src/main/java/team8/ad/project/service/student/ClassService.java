package team8.ad.project.service.student;

import team8.ad.project.entity.dto.ClassListItemDTO;
import team8.ad.project.entity.dto.ListDTO;
import team8.ad.project.entity.dto.LoginDTO;
import team8.ad.project.entity.vo.LoginResultVO;

import javax.servlet.http.HttpSession;

public interface ClassService {
    ListDTO<ClassListItemDTO> viewClass();

     /**
     * 加入班级
     * @param accessType byLink 或 byName
     * @param key byLink=token；byName=班级名称
     * @return 如果为 null 表示成功；否则返回失败原因
     */
    String joinClass(String accessType, String key);
    
    String leaveClass(Integer classId);


    LoginResultVO login(LoginDTO loginDTO, HttpSession session);
}
