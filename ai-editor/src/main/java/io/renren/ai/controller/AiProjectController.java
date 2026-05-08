package io.renren.ai.controller;

import io.renren.ai.entity.AiProject;
import io.renren.ai.entity.AiProjectVersion;
import io.renren.ai.service.AiProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/project")
@RequiredArgsConstructor
public class AiProjectController {

    private final AiProjectService projectService;

    private static final Long DEFAULT_TENANT = 1L;

    @GetMapping("/list")
    public Map<String, Object> list() {
        List<AiProject> list = projectService.listByTenant(DEFAULT_TENANT);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> info(@PathVariable("id") Long id) {
        AiProject project = projectService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", project);
        return result;
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody AiProject project) {
        projectService.create(project);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "创建成功");
        return result;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody AiProject project) {
        projectService.update(project);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "更新成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        projectService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "删除成功");
        return result;
    }

    // ============ 版本相关 ============

    @GetMapping("/{id}/version/list")
    public Map<String, Object> versionList(@PathVariable("id") Long projectId) {
        List<AiProjectVersion> list = projectService.getVersionList(projectId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    @PostMapping("/{id}/version")
    public Map<String, Object> createVersion(@PathVariable("id") Long projectId, @RequestBody AiProjectVersion version) {
        projectService.createVersion(projectId, version);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "创建成功");
        return result;
    }

    @PutMapping("/{id}/version/{vid}/switch")
    public Map<String, Object> switchVersion(@PathVariable("id") Long projectId, @PathVariable("vid") Long versionId) {
        projectService.switchVersion(projectId, versionId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "切换成功");
        return result;
    }

    @DeleteMapping("/{id}/version/{vid}")
    public Map<String, Object> deleteVersion(@PathVariable("id") Long projectId, @PathVariable("vid") Long versionId) {
        projectService.deleteVersion(versionId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "删除成功");
        return result;
    }
}
