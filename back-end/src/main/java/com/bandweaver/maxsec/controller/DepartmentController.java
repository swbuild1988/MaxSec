package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.Department;
import com.bandweaver.maxsec.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/departments")
    public R getDepartments() {
        return new R(departmentService.selectAll());
    }

    @PostMapping("/departments")
    public R addDepartments(@RequestBody Department department) {
        departmentService.insert(department);
        return new R();
    }

    @PostMapping("/departments_edit")
    public R editDepartments(@RequestBody Department department) {
        departmentService.update(department);
        return new R();
    }

    @GetMapping("/departments_delete/{id}")
    public R deleteDepartment(@PathVariable("id") Integer id) {
        departmentService.delete(id);
        return new R();
    }

    @GetMapping("/departments/{id}")
    public R getDepartment(@PathVariable Integer id) {
        return new R(departmentService.selectByPrimaryKey(id));
    }
}
