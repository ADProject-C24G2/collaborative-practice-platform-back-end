package team8.ad.project.service.teacher.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.ClassDTO;
import team8.ad.project.mapper.teacher.ClassMapper;
import team8.ad.project.service.teacher.ClassService;
import team8.ad.project.entity.entity.Class;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClassServiceImpl implements ClassService {


    @Autowired
    ClassMapper classMapper;

    /**
     *Create a new Class
     * @param classDTO
     */
    @Override
    public String createClass(ClassDTO classDTO) {
        String token = null;
        Class myClass = new Class();
        BeanUtils.copyProperties(classDTO, myClass);

        if(classDTO.getAccessType().equals("byLink") ){
            token = UUID.randomUUID().toString();
            myClass.setToken(token);
        }
            setTime(classDTO,myClass);
            classMapper.insert(myClass);
        // TODO Remember not define
        return token;

    }

    private void setTime(ClassDTO classDTO, Class myClass){
        switch(classDTO.getExpirationTime()){
            case 2: // 2 days
                myClass.setAccessExpiration(LocalDateTime.now().plusDays(2));
                break;
            case 5: // 5 days
                myClass.setAccessExpiration(LocalDateTime.now().plusDays(5));
                break;
            case 30: // 30 days
                myClass.setAccessExpiration(LocalDateTime.now().plusDays(30));
                break;
            case 0: // Permanent (Never)
                myClass.setAccessExpiration(null); // No expiration
                break;
            default:
                throw new IllegalArgumentException("Invalid expiration time: " + classDTO.getExpirationTime());

        }

    }




}
