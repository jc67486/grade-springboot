package com.zhang.grade.controller;

import com.zhang.grade.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("status", "up");
        m.put("ts", System.currentTimeMillis());
        return Result.ok(m);
    }
}
