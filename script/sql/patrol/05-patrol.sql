create table if not exists patrol_device (
    device_id           varchar(64)    not null comment '设备ID',
    tenant_id           varchar(20)    default '000000' comment '租户编号',
    device_name         varchar(100)   not null comment '设备名称',
    device_type         varchar(32)    default 'HEADSET' comment '设备类型',
    service_uuid        varchar(100)   default null comment 'BLE服务UUID',
    mac_address         varchar(64)    default null comment '设备MAC',
    bonded              tinyint(1)     default 0 comment '是否已绑定',
    online              tinyint(1)     default 0 comment '是否在线',
    battery_percent     int            default 0 comment '电量',
    signal_bars         int            default 0 comment '信号格数',
    online_duration     varchar(32)    default '00:00:00' comment '在线时长',
    storage_used_gb     decimal(10,2)  default 0 comment '已用存储GB',
    storage_total_gb    decimal(10,2)  default 0 comment '总存储GB',
    firmware_version    varchar(32)    default null comment '固件版本',
    recording_status    varchar(32)    default 'IDLE' comment '录制状态',
    talking             tinyint(1)     default 0 comment '对讲状态',
    cloud_connected     tinyint(1)     default 0 comment '云端连接状态',
    latitude            decimal(10,7)  default null comment '纬度',
    longitude           decimal(10,7)  default null comment '经度',
    address             varchar(255)   default null comment '位置描述',
    last_heartbeat_time datetime       default null comment '最后心跳时间',
    create_dept         bigint(20)     default null comment '创建部门',
    create_by           bigint(20)     default null comment '创建者',
    create_time         datetime       default null comment '创建时间',
    update_by           bigint(20)     default null comment '更新者',
    update_time         datetime       default null comment '更新时间',
    del_flag            char(1)        default '0' comment '删除标志',
    primary key (device_id),
    key idx_patrol_device_tenant_online (tenant_id, online)
) engine=innodb default charset=utf8mb4 comment='巡检设备表';

create table if not exists patrol_alert (
    alert_id       varchar(64)   not null comment '告警ID',
    tenant_id      varchar(20)   default '000000' comment '租户编号',
    title          varchar(120)  not null comment '标题',
    level          varchar(32)   not null comment '级别',
    status         varchar(32)   not null comment '状态',
    occurred_at    varchar(32)   default null comment '发生时间',
    location_text  varchar(255)  default null comment '位置',
    source         varchar(64)   default null comment '来源',
    description    varchar(1000) default null comment '描述',
    confidence     varchar(32)   default null comment '置信度',
    close_result   varchar(64)   default null comment '关闭结果',
    close_note     varchar(1000) default null comment '关闭说明',
    operator_id    varchar(64)   default null comment '操作人',
    create_dept    bigint(20)    default null comment '创建部门',
    create_by      bigint(20)    default null comment '创建者',
    create_time    datetime      default null comment '创建时间',
    update_by      bigint(20)    default null comment '更新者',
    update_time    datetime      default null comment '更新时间',
    del_flag       char(1)       default '0' comment '删除标志',
    primary key (alert_id),
    key idx_patrol_alert_tenant_status (tenant_id, status)
) engine=innodb default charset=utf8mb4 comment='巡检告警表';

create table if not exists patrol_media (
    media_id          bigint(20)    not null comment '媒体主键',
    tenant_id         varchar(20)   default '000000' comment '租户编号',
    file_id           varchar(64)   not null comment '客户端文件ID',
    file_name         varchar(255)  not null comment '文件名',
    media_type        varchar(32)   not null comment '媒体类型',
    captured_at       varchar(32)   default null comment '采集时间',
    size_text         varchar(32)   default null comment '大小描述',
    duration_text     varchar(32)   default null comment '时长描述',
    sha256_verified   tinyint(1)    default 0 comment '校验结果',
    storage_side      varchar(32)   not null comment '存储侧',
    transfer_status   varchar(32)   default 'IDLE' comment '传输状态',
    progress          decimal(5,2)  default 0 comment '传输进度',
    content_uri       varchar(500)  default null comment '客户端URI',
    oss_id            bigint(20)    default null comment 'OSS记录ID',
    bucket_name       varchar(100)  default null comment '对象桶',
    object_key        varchar(500)  default null comment '对象Key',
    sha256            varchar(128)  default null comment 'SHA256',
    create_dept       bigint(20)    default null comment '创建部门',
    create_by         bigint(20)    default null comment '创建者',
    create_time       datetime      default null comment '创建时间',
    update_by         bigint(20)    default null comment '更新者',
    update_time       datetime      default null comment '更新时间',
    del_flag          char(1)       default '0' comment '删除标志',
    primary key (media_id),
    unique key uk_patrol_media_file_side (tenant_id, file_id, storage_side),
    key idx_patrol_media_tenant_side (tenant_id, storage_side)
) engine=innodb default charset=utf8mb4 comment='巡检媒体证据表';

create table if not exists patrol_area (
    area_id       varchar(64)   not null comment '巡区ID',
    tenant_id     varchar(20)   default '000000' comment '租户编号',
    area_name     varchar(120)  not null comment '巡区名称',
    team_id       varchar(64)   default null comment '队伍ID',
    team_name     varchar(120)  default null comment '队伍名称',
    boundary_json text          comment '边界点JSON',
    route_json    text          comment '路线点JSON',
    create_dept   bigint(20)    default null comment '创建部门',
    create_by     bigint(20)    default null comment '创建者',
    create_time   datetime      default null comment '创建时间',
    update_by     bigint(20)    default null comment '更新者',
    update_time   datetime      default null comment '更新时间',
    del_flag      char(1)       default '0' comment '删除标志',
    primary key (area_id)
) engine=innodb default charset=utf8mb4 comment='巡区表';

create table if not exists patrol_sos_event (
    sos_id             varchar(64)   not null comment 'SOS ID',
    tenant_id          varchar(20)   default '000000' comment '租户编号',
    phase              varchar(32)   not null comment '阶段',
    message            varchar(255)  default null comment '消息',
    latitude           decimal(10,7) default null comment '纬度',
    longitude          decimal(10,7) default null comment '经度',
    accuracy_meters    decimal(10,2) default null comment '精度',
    address            varchar(255)  default null comment '地址',
    recording_audio    tinyint(1)    default 0 comment '是否录音',
    backup_eta_minutes int           default null comment '支援ETA',
    create_dept        bigint(20)    default null comment '创建部门',
    create_by          bigint(20)    default null comment '创建者',
    create_time        datetime      default null comment '创建时间',
    update_by          bigint(20)    default null comment '更新者',
    update_time        datetime      default null comment '更新时间',
    del_flag           char(1)       default '0' comment '删除标志',
    primary key (sos_id),
    key idx_patrol_sos_tenant_phase (tenant_id, phase)
) engine=innodb default charset=utf8mb4 comment='SOS事件表';

insert ignore into sys_user values(9527, '000000', 103, 'POLICE_9527', '张警官', 'sys_user', 'zhang.police@city.gov.cn', '13800009527', '0', null, '$2a$10$X7Dwu6JiORKduaP8iS9sOOgUk/w93X63gAg2XAGGAXRs0KbiaNSki', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '移动端巡逻警员');
insert ignore into sys_user_role values ('9527', '3');
insert ignore into sys_user_post values ('9527', '4');

insert ignore into patrol_device(device_id, tenant_id, device_name, device_type, service_uuid, mac_address, bonded, online, battery_percent, signal_bars, online_duration, storage_used_gb, storage_total_gb, firmware_version, recording_status, talking, cloud_connected, latitude, longitude, address, last_heartbeat_time, create_dept, create_by, create_time, del_flag)
values
('HEADSET_001', '000000', 'ForceLink-H1', 'HEADSET', '0000-pl2-ble-control', '2C:4A:91:3F:8B:02', 1, 1, 88, 4, '02:45:12', 42.5, 128, 'v1.2.4', 'IDLE', 0, 1, 26.1002000, 119.3065500, '福州温泉公园', sysdate(), 103, 1, sysdate(), '0'),
('RECORDER_A5', '000000', 'ForceLink-A5', 'RECORDER', '0000-pl2-ble-control', '4F:02:8C:76:A1:19', 0, 0, 72, 3, '00:00:00', 12.1, 64, 'v1.0.9', 'IDLE', 0, 0, 26.1005800, 119.3077100, '福州温泉公园东门', null, 103, 1, sysdate(), '0'),
('SENSOR_S9', '000000', 'ForceLink-S9', 'SENSOR', '0000-pl2-ble-control', '1E:BD:55:0A:44:71', 0, 0, 65, 2, '00:00:00', 2.4, 16, 'v1.1.1', 'IDLE', 0, 0, 26.1015500, 119.3090000, '核心商务区 CBD-North', null, 103, 1, sysdate(), '0'),
('GLASSES_G1', '000000', 'ForceLink-G1', 'GLASSES', '0000-pl2-ble-control', '6B:13:9E:41:D7:50', 0, 0, 91, 4, '00:00:00', 24.8, 128, 'v1.3.0', 'IDLE', 0, 0, 26.1025500, 119.3079500, '北侧周界入口', null, 103, 1, sysdate(), '0');

insert ignore into patrol_alert(alert_id, tenant_id, title, level, status, occurred_at, location_text, source, description, confidence, create_dept, create_by, create_time, del_flag)
values
('AL-99824-03', '000000', '非法侵入监测', 'CRITICAL', 'PENDING', '14:32', '西三区 4号围墙 节点B', 'CAM-042', '围墙节点 B 检测到人员越界，耳机端已同步 12 秒现场视频片段。', '98.4%', 103, 1, sysdate(), '0'),
('AL-99824-04', '000000', '未识别车辆靠近', 'WARNING', 'PENDING', '14:38', '北侧周界入口', 'RFID-09', '车牌识别失败，建议现场复核并记录车辆去向。', '91.2%', 103, 1, sysdate(), '0'),
('AL-99821-11', '000000', '夜间巡查异常声源', 'INFO', 'CLOSED', '13:22', '核心商务区 CBD-North', 'HEADSET_001', '环境音频超过阈值，现场确认无风险。', '74.8%', 103, 1, sysdate(), '0');

insert ignore into patrol_media(media_id, tenant_id, file_id, file_name, media_type, captured_at, size_text, duration_text, sha256_verified, storage_side, transfer_status, progress, bucket_name, object_key, create_dept, create_by, create_time, del_flag)
values
(190000000000000001, '000000', 'VID-042', 'CAM_04_A', 'VIDEO', '14:22:05', '84.1 MB', '04:12', 1, 'DEVICE', 'IDLE', 0, 'patrol-media', 'device/VID-042', 103, 1, sysdate(), '0'),
(190000000000000002, '000000', 'IMG-8821', 'IMG_8821', 'PHOTO', '14:45:12', '2.4 MB', null, 1, 'DEVICE', 'DONE', 1, 'patrol-media', 'device/IMG-8821', 103, 1, sysdate(), '0'),
(190000000000000003, '000000', 'AUD-318', 'VOICE_318', 'AUDIO', '14:50:02', '8.6 MB', '03:55', 1, 'PHONE', 'IDLE', 0, 'patrol-evidence', 'phone/AUD-318', 103, 1, sysdate(), '0'),
(190000000000000004, '000000', 'VID-051', 'PATROL_051', 'VIDEO', '15:02:18', '126 MB', '08:12', 0, 'PHONE', 'IDLE', 0, 'patrol-evidence', 'phone/VID-051', 103, 1, sysdate(), '0');

insert ignore into patrol_area(area_id, tenant_id, area_name, team_id, team_name, boundary_json, route_json, create_dept, create_by, create_time, del_flag)
values ('AREA-FZ-WQ-001', '000000', '福州温泉公园重点巡区', 'TEAM-A-42', '巡逻组 A-42',
'[{"latitude":26.10295,"longitude":119.30485},{"latitude":26.10335,"longitude":119.31010},{"latitude":26.10020,"longitude":119.31115},{"latitude":26.09795,"longitude":119.30910},{"latitude":26.09815,"longitude":119.30465}]',
'[{"latitude":26.09875,"longitude":119.30495},{"latitude":26.10020,"longitude":119.30655},{"latitude":26.10058,"longitude":119.30771},{"latitude":26.10155,"longitude":119.30900},{"latitude":26.10255,"longitude":119.30795}]',
103, 1, sysdate(), '0');
