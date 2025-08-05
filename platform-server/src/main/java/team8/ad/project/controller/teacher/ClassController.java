package team8.ad.project.controller.teacher;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team8.ad.project.context.BaseContext;
import team8.ad.project.entity.dto.ClassDTO;
import team8.ad.project.result.Result;
import team8.ad.project.service.teacher.ClassService;

@RestController
@RequestMapping("/teacher/class")
@Api(tags = "Class-related interfaces")
@Slf4j
public class ClassController {

    @Autowired
    private ClassService classService;



    /**
     * Create a new class
     * @param classDTO
     * @return
     */
    @PostMapping("/create")
    @ApiOperation("Create Class")
    public Result createClass(@RequestBody ClassDTO classDTO){

        log.info("Create Class:{}", classDTO);
        // TODO Here need set the teacherId
        BaseContext.setCurrentId(1);

        String token = classService.createClass(classDTO);

        return Result.success(token);

    }
}
