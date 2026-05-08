package io.renren.ai.controller;

import io.renren.ai.entity.AiPage;
import io.renren.ai.service.AiPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/page")
@RequiredArgsConstructor
public class AiPageController {

    private final AiPageService pageService;

    private static final Long DEFAULT_TENANT = 1L;

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) Long projectId) {
        List<AiPage> list;
        if (projectId != null) {
            list = pageService.listByProjectId(projectId);
        } else {
            list = pageService.listByTenant(DEFAULT_TENANT);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> info(@PathVariable("id") Long id) {
        AiPage page = pageService.getById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", page);
        return result;
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody AiPage page) {
        pageService.create(page);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "创建成功");
        return result;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody AiPage page) {
        pageService.update(page);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "更新成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        pageService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "删除成功");
        return result;
    }

    @PutMapping("/{id}/publish")
    public Map<String, Object> publish(@PathVariable("id") Long id) {
        AiPage page = pageService.getById(id);
        if (page != null) {
            page.setIsPublished(1);
            pageService.update(page);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "发布成功");
        return result;
    }
}
