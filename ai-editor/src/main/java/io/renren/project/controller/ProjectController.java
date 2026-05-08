package io.renren.project.controller;

import io.renren.project.entity.Project;
import io.renren.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private Long getTenantId() {
        return 1L;
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        List<Project> list = projectService.listByTenantId(getTenantId());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Project project = projectService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", project);
        return result;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Project project) {
        project.setTenantId(getTenantId());
        Project created = projectService.create(project);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", created);
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        project.setTenantId(getTenantId());
        Project updated = projectService.update(project);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", updated);
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean success = projectService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 0 : 1);
        result.put("msg", success ? "删除成功" : "删除失败");
        return result;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Long count = projectService.countByTenantId(getTenantId());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", Map.of("total", count));
        return result;
    }

    @GetMapping("/recent-operations")
    public Map<String, Object> recentOperations() {
        List<Map<String, Object>> ops = projectService.recentOperations(getTenantId());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", ops);
        return result;
    }
}
