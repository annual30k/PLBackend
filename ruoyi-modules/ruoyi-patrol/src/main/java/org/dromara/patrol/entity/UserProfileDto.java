package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前用户资料
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 警号
     */
    private String badgeNo;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 执勤区域
     */
    private String dutyArea;

    /**
     * 当前班次时长
     */
    private String shiftDuration;

    /**
     * 巡逻分组
     */
    private String patrolGroup;

    /**
     * 系统节点
     */
    private String systemNode;
}
