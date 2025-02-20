package com.dkd.manage.domain.dto;

import lombok.Data;

import java.util.List;
@Data
public class TaskDto {
    private Long createType;// 创建类型
    private String innerCode;// 设备编号
    private Long userId;// 执行人ID
    private Long assignorId;// 指派人ID
    private Long productTypeId;// 工单类型
    private String desc;// 描述信息
    private List<TaskDetailsDto> details;// 工单详情（只有补货工单才涉及）
}
