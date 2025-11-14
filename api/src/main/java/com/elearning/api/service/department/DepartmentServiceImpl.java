package com.elearning.api.service.department;

import com.elearning.common.domain.dept.Department;
import com.elearning.common.domain.dept.DepartmentRepository;
import com.elearning.common.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department createdDepartment(String departmentName) {
        return departmentRepository.findDepartmentByName(departmentName)
                .orElseGet(() -> {
                    Department department = Department.builder()
                            .name(departmentName)
                            .status(Status.NORMAL)
                            .build();
                    return departmentRepository.save(department);
                });
    }
}
