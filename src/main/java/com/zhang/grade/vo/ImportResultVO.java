package com.zhang.grade.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportResultVO {
    private int total;
    private int success;
    private int failed;
    private long costMs;
}
