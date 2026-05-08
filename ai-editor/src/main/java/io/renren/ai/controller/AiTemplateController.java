package io.renren.ai.controller;

import io.renren.ai.entity.AiTemplate;
import io.renren.ai.service.AiTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/template")
@RequiredArgsConstructor
public class AiTemplateController {

    private final AiTemplateService templateService;

    private static final Long DEFAULT_TENANT = 1L;

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) Integer type) {
        List<AiTemplate> list;
        if (type != null) {
            list = templateService.listByType(DEFAULT_TENANT, type);
        } else {
            list = templateService.listByTenant(DEFAULT_TENANT);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    @GetMapping("/official")
    public Map<String, Object> officialList() {
        List<AiTemplate> list = templateService.listOfficial(DEFAULT_TENANT);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> info(@PathVariable("id") Long id) {
        AiTemplate template = templateService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", template);
        return result;
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody AiTemplate template) {
        templateService.create(template);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "创建成功");
        return result;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody AiTemplate template) {
        templateService.update(template);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "更新成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        templateService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "删除成功");
        return result;
    }

    @PostMapping("/save-from-generate")
    public Map<String, Object> saveFromGenerate(@RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        String code = (String) params.get("code");
        AiTemplate template = templateService.saveFromGenerate(name, code, DEFAULT_TENANT);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "保存成功");
        result.put("data", template);
        return result;
    }

    @PostMapping("/{id}/use")
    public Map<String, Object> use(@PathVariable("id") Long id) {
        templateService.incrementUsage(id);
        AiTemplate template = templateService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "使用成功");
        result.put("data", template);
        return result;
    }
}
