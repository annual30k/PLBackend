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

create table if not exists patrol_alert_attachment (
    attachment_id  varchar(64)  not null comment '附件ID',
    tenant_id      varchar(20)  default '000000' comment '租户编号',
    alert_id       varchar(64)  not null comment '告警ID',
    client_file_id varchar(64)  default null comment '客户端文件ID',
    file_name      varchar(255) default null comment '文件名',
    mime_type      varchar(100) default null comment 'MIME类型',
    size_bytes     bigint(20)   default null comment '文件大小',
    source         varchar(32)  default null comment '文件来源',
    local_uri      varchar(500) default null comment '端侧本地URI',
    upload_intent  varchar(64)  default null comment '上传用途',
    create_dept    bigint(20)   default null comment '创建部门',
    create_by      bigint(20)   default null comment '创建者',
    create_time    datetime     default null comment '创建时间',
    update_by      bigint(20)   default null comment '更新者',
    update_time    datetime     default null comment '更新时间',
    del_flag       char(1)      default '0' comment '删除标志',
    primary key (attachment_id),
    key idx_patrol_alert_attachment_alert (tenant_id, alert_id)
) engine=innodb default charset=utf8mb4 comment='预警处置附件表';

create table if not exists patrol_alert_disposition (
    disposition_id    varchar(64)   not null comment '处置流水ID',
    tenant_id         varchar(20)   default '000000' comment '租户编号',
    alert_id          varchar(64)   not null comment '告警ID',
    action_type       varchar(32)   not null comment '动作类型',
    action_result     varchar(64)   default null comment '动作结果',
    operator_id       varchar(64)   default null comment '操作人ID',
    operator_name     varchar(64)   default null comment '操作人名称',
    note              varchar(1000) default null comment '处置说明',
    attachments_count int           default 0 comment '附件数量',
    occurred_at       datetime      default null comment '发生时间',
    create_dept       bigint(20)    default null comment '创建部门',
    create_by         bigint(20)    default null comment '创建者',
    create_time       datetime      default null comment '创建时间',
    update_by         bigint(20)    default null comment '更新者',
    update_time       datetime      default null comment '更新时间',
    del_flag          char(1)       default '0' comment '删除标志',
    primary key (disposition_id),
    key idx_patrol_alert_disposition_alert (tenant_id, alert_id, occurred_at)
) engine=innodb default charset=utf8mb4 comment='预警处置流水表';

create table if not exists patrol_media (
    media_id          bigint(20)    not null comment '媒体主键',
    tenant_id         varchar(20)   default '000000' comment '租户编号',
    file_id           varchar(64)   not null comment '客户端文件ID',
    file_name         varchar(255)  not null comment '文件名',
    media_type        varchar(32)   not null comment '媒体类型',
    captured_at       varchar(32)   default null comment '采集时间',
    size_text         varchar(32)   default null comment '大小描述',
    file_size_bytes   bigint(20)    default null comment '文件大小字节',
    mime_type         varchar(128)  default null comment 'MIME类型',
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
    watermark_token   varchar(128)  default null comment '证据水印令牌',
    badge_no          varchar(64)   default null comment '警号',
    officer_name      varchar(120)  default null comment '警员姓名',
    device_id         varchar(64)   default null comment '设备ID',
    biz_type          varchar(64)   default null comment '业务类型',
    biz_id            varchar(64)   default null comment '业务ID',
    evidence_source   varchar(64)   default null comment '证据来源',
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

create table if not exists patrol_media_upload_task (
    task_id           varchar(64)   not null comment '上传任务ID',
    tenant_id         varchar(20)   default '000000' comment '租户编号',
    file_id           varchar(64)   default null comment '合并后媒体文件ID',
    file_name         varchar(255)  not null comment '原始文件名',
    media_type        varchar(32)   default null comment '媒体类型',
    mime_type         varchar(128)  default null comment 'MIME类型',
    file_size_bytes   bigint(20)    default 0 comment '文件总大小字节',
    chunk_size_bytes  bigint(20)    default 0 comment '分片大小字节',
    total_chunks      int           default 0 comment '分片总数',
    uploaded_chunks   int           default 0 comment '已上传分片数',
    uploaded_bytes    bigint(20)    default 0 comment '已上传字节数',
    expected_sha256   varchar(128)  default null comment '端侧声明SHA256',
    actual_sha256     varchar(128)  default null comment '服务端实际SHA256',
    storage_side      varchar(32)   default 'PHONE' comment '存储侧',
    biz_type          varchar(64)   default null comment '业务类型',
    biz_id            varchar(64)   default null comment '业务ID',
    status            varchar(32)   default 'INIT' comment '任务状态',
    progress          decimal(5,4)  default 0 comment '上传进度',
    temp_dir          varchar(512)  default null comment '临时分片目录',
    error_message     varchar(500)  default null comment '失败原因',
    badge_no          varchar(64)   default null comment '警号',
    officer_name      varchar(120)  default null comment '警员姓名',
    device_id         varchar(64)   default null comment '设备ID',
    completed_at      datetime      default null comment '完成时间',
    create_dept       bigint(20)    default null comment '创建部门',
    create_by         bigint(20)    default null comment '创建者',
    create_time       datetime      default null comment '创建时间',
    update_by         bigint(20)    default null comment '更新者',
    update_time       datetime      default null comment '更新时间',
    del_flag          char(1)       default '0' comment '删除标志',
    primary key (task_id),
    key idx_patrol_upload_task_status (tenant_id, status),
    key idx_patrol_upload_task_file (tenant_id, file_id)
) engine=innodb default charset=utf8mb4 comment='巡检媒体分片上传任务表';

drop procedure if exists add_patrol_column_if_absent;
delimiter //
create procedure add_patrol_column_if_absent(in p_table varchar(64), in p_column varchar(64), in p_definition text)
begin
    if not exists (
        select 1 from information_schema.columns
        where table_schema = database()
          and table_name = p_table
          and column_name = p_column
    ) then
        set @ddl = concat('alter table ', p_table, ' add column ', p_column, ' ', p_definition);
        prepare stmt from @ddl;
        execute stmt;
        deallocate prepare stmt;
    end if;
end//
delimiter ;
call add_patrol_column_if_absent('patrol_media', 'file_size_bytes', 'bigint(20) default null comment ''文件大小字节''');
call add_patrol_column_if_absent('patrol_media', 'mime_type', 'varchar(128) default null comment ''MIME类型''');
call add_patrol_column_if_absent('patrol_media', 'watermark_token', 'varchar(128) default null comment ''证据水印令牌''');
call add_patrol_column_if_absent('patrol_media', 'badge_no', 'varchar(64) default null comment ''警号''');
call add_patrol_column_if_absent('patrol_media', 'officer_name', 'varchar(120) default null comment ''警员姓名''');
call add_patrol_column_if_absent('patrol_media', 'device_id', 'varchar(64) default null comment ''设备ID''');
call add_patrol_column_if_absent('patrol_media', 'biz_type', 'varchar(64) default null comment ''业务类型''');
call add_patrol_column_if_absent('patrol_media', 'biz_id', 'varchar(64) default null comment ''业务ID''');
call add_patrol_column_if_absent('patrol_media', 'evidence_source', 'varchar(64) default null comment ''证据来源''');
drop procedure if exists add_patrol_column_if_absent;

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

create table if not exists patrol_sos_disposition (
    disposition_id       varchar(64)   not null comment '流水ID',
    tenant_id            varchar(20)   default '000000' comment '租户编号',
    sos_id               varchar(64)   not null comment 'SOS ID',
    action_type          varchar(64)   not null comment '动作类型',
    action_result        varchar(64)   default null comment '处置结果',
    operator_id          varchar(64)   default null comment '操作人ID',
    operator_name        varchar(120)  default null comment '操作人姓名',
    note                 varchar(500)  default null comment '处置说明',
    contact_name         varchar(120)  default null comment '通知联系人',
    contact_phone        varchar(64)   default null comment '联系电话',
    attachment_file_id   varchar(64)   default null comment '附件文件ID',
    attachment_file_name varchar(255)  default null comment '附件文件名',
    backup_eta_minutes   int           default null comment '增援ETA',
    occurred_at          datetime      default null comment '发生时间',
    create_dept          bigint(20)    default null comment '创建部门',
    create_by            bigint(20)    default null comment '创建者',
    create_time          datetime      default null comment '创建时间',
    update_by            bigint(20)    default null comment '更新者',
    update_time          datetime      default null comment '更新时间',
    del_flag             char(1)       default '0' comment '删除标志',
    primary key (disposition_id),
    key idx_patrol_sos_disposition_sos (tenant_id, sos_id, occurred_at)
) engine=innodb default charset=utf8mb4 comment='SOS处置流水表';

create table if not exists patrol_location_track (
    track_id        varchar(64)   not null comment '轨迹ID',
    tenant_id       varchar(20)   default '000000' comment '租户编号',
    badge_no        varchar(64)   default null comment '警号',
    officer_name    varchar(64)   default null comment '警员姓名',
    device_id       varchar(64)   not null comment '设备ID',
    latitude        decimal(10,7) not null comment '纬度',
    longitude       decimal(10,7) not null comment '经度',
    accuracy_meters decimal(10,2) default null comment '定位精度',
    address         varchar(255)  default null comment '地址',
    reported_at     datetime      default null comment '上报时间',
    create_dept     bigint(20)    default null comment '创建部门',
    create_by       bigint(20)    default null comment '创建者',
    create_time     datetime      default null comment '创建时间',
    update_by       bigint(20)    default null comment '更新者',
    update_time     datetime      default null comment '更新时间',
    del_flag        char(1)       default '0' comment '删除标志',
    primary key (track_id),
    key idx_patrol_track_badge_time (tenant_id, badge_no, reported_at),
    key idx_patrol_track_device_time (tenant_id, device_id, reported_at)
) engine=innodb default charset=utf8mb4 comment='警力位置轨迹表';

create table if not exists patrol_device_command (
    command_id     varchar(64)   not null comment '指令ID',
    tenant_id      varchar(20)   default '000000' comment '租户编号',
    device_id      varchar(64)   not null comment '设备ID',
    command        varchar(64)   not null comment '指令',
    operator_id    varchar(64)   default null comment '操作人',
    request_id     varchar(128)  default null comment '请求ID',
    status         varchar(32)   not null comment '状态',
    result_message varchar(500)  default null comment '结果消息',
    sent_at        datetime      default null comment '下发时间',
    ack_at         datetime      default null comment '回执时间',
    create_dept    bigint(20)    default null comment '创建部门',
    create_by      bigint(20)    default null comment '创建者',
    create_time    datetime      default null comment '创建时间',
    update_by      bigint(20)    default null comment '更新者',
    update_time    datetime      default null comment '更新时间',
    del_flag       char(1)       default '0' comment '删除标志',
    primary key (command_id),
    key idx_patrol_command_device_time (tenant_id, device_id, sent_at),
    key idx_patrol_command_status (tenant_id, status)
) engine=innodb default charset=utf8mb4 comment='设备指令记录表';

create table if not exists patrol_device_event (
    event_id     varchar(64)   not null comment '事件ID',
    tenant_id    varchar(20)   default '000000' comment '租户编号',
    device_id    varchar(64)   not null comment '设备ID',
    event_type   varchar(64)   not null comment '事件类型',
    event_level  varchar(32)   not null comment '事件级别',
    event_title  varchar(120)  not null comment '事件标题',
    event_detail varchar(1000) default null comment '事件详情',
    occurred_at  datetime      default null comment '发生时间',
    create_dept  bigint(20)    default null comment '创建部门',
    create_by    bigint(20)    default null comment '创建者',
    create_time  datetime      default null comment '创建时间',
    update_by    bigint(20)    default null comment '更新者',
    update_time  datetime      default null comment '更新时间',
    del_flag     char(1)       default '0' comment '删除标志',
    primary key (event_id),
    key idx_patrol_device_event_device_time (tenant_id, device_id, occurred_at),
    key idx_patrol_device_event_type (tenant_id, event_type)
) engine=innodb default charset=utf8mb4 comment='设备事件日志表';

create table if not exists patrol_device_binding (
    binding_id  varchar(64)  not null comment '绑定ID',
    tenant_id   varchar(20)  default '000000' comment '租户编号',
    device_id   varchar(64)  not null comment '设备ID',
    user_id     bigint(20)   default null comment '警员用户ID',
    user_name   varchar(64)  default null comment '登录账号/警号',
    nick_name   varchar(64)  default null comment '警员姓名',
    dept_id     bigint(20)   default null comment '所属部门ID',
    dept_name   varchar(120) default null comment '所属部门名称',
    badge_no    varchar(64)  default null comment '警号',
    bind_status varchar(32)  not null comment '绑定状态',
    bound_at    datetime     default null comment '绑定时间',
    unbound_at  datetime     default null comment '解绑时间',
    remark      varchar(500) default null comment '备注',
    create_dept bigint(20)   default null comment '创建部门',
    create_by   bigint(20)   default null comment '创建者',
    create_time datetime     default null comment '创建时间',
    update_by   bigint(20)   default null comment '更新者',
    update_time datetime     default null comment '更新时间',
    del_flag    char(1)      default '0' comment '删除标志',
    primary key (binding_id),
    key idx_patrol_device_binding_device (tenant_id, device_id, bind_status),
    key idx_patrol_device_binding_user (tenant_id, user_name, bind_status)
) engine=innodb default charset=utf8mb4 comment='设备警员绑定表';

create table if not exists patrol_message (
    message_id  varchar(64)   not null comment '消息ID',
    tenant_id   varchar(20)   default '000000' comment '租户编号',
    title       varchar(120)  not null comment '标题',
    content     varchar(1000) not null comment '内容',
    target_type varchar(32)   not null comment '目标类型',
    target_id   varchar(64)   not null comment '目标ID',
    target_name varchar(120)  default null comment '目标名称',
    channel     varchar(32)   default 'APP' comment '通道',
    status      varchar(32)   not null comment '状态',
    read_count  int           default 0 comment '已读数',
    total_count int           default 1 comment '总数',
    sent_at     datetime      default null comment '发送时间',
    create_dept bigint(20)    default null comment '创建部门',
    create_by   bigint(20)    default null comment '创建者',
    create_time datetime      default null comment '创建时间',
    update_by   bigint(20)    default null comment '更新者',
    update_time datetime      default null comment '更新时间',
    del_flag    char(1)       default '0' comment '删除标志',
    primary key (message_id),
    key idx_patrol_message_target (tenant_id, target_type, target_id),
    key idx_patrol_message_time (tenant_id, sent_at)
) engine=innodb default charset=utf8mb4 comment='指挥消息表';

create table if not exists patrol_message_receipt (
    receipt_id      varchar(64)  not null comment '接收明细ID',
    tenant_id       varchar(20)  default '000000' comment '租户编号',
    message_id      varchar(64)  not null comment '消息ID',
    recipient_id    varchar(64)  not null comment '接收人警号或目标标识',
    recipient_name  varchar(120) default null comment '接收人名称',
    device_id       varchar(64)  default null comment '绑定设备ID',
    delivery_status varchar(32)  not null comment '投递状态',
    delivered_at    datetime     default null comment '投递时间',
    read_at         datetime     default null comment '已读时间',
    last_pull_at    datetime     default null comment '最近拉取时间',
    create_dept     bigint(20)   default null comment '创建部门',
    create_by       bigint(20)   default null comment '创建者',
    create_time     datetime     default null comment '创建时间',
    update_by       bigint(20)   default null comment '更新者',
    update_time     datetime     default null comment '更新时间',
    del_flag        char(1)      default '0' comment '删除标志',
    primary key (receipt_id),
    unique key uk_patrol_message_receipt_user (tenant_id, message_id, recipient_id),
    key idx_patrol_message_receipt_message (tenant_id, message_id),
    key idx_patrol_message_receipt_target (tenant_id, recipient_id, device_id, delivery_status)
) engine=innodb default charset=utf8mb4 comment='指挥消息接收明细表';

create table if not exists patrol_audit_log (
    log_id        varchar(64)  not null comment '日志ID',
    tenant_id     varchar(20)  default '000000' comment '租户编号',
    log_type      varchar(32)  not null comment '日志类型',
    operator_name varchar(64)  default null comment '操作人',
    action        varchar(120) not null comment '动作',
    resource      varchar(128) default null comment '资源',
    result        varchar(32)  not null comment '结果',
    ip_address    varchar(64)  default null comment 'IP',
    trace_id      varchar(128) default null comment '链路ID',
    occurred_at   datetime     default null comment '发生时间',
    create_dept   bigint(20)   default null comment '创建部门',
    create_by     bigint(20)   default null comment '创建者',
    create_time   datetime     default null comment '创建时间',
    update_by     bigint(20)   default null comment '更新者',
    update_time   datetime     default null comment '更新时间',
    del_flag      char(1)      default '0' comment '删除标志',
    primary key (log_id),
    key idx_patrol_audit_time (tenant_id, occurred_at),
    key idx_patrol_audit_resource (tenant_id, resource)
) engine=innodb default charset=utf8mb4 comment='指挥后台审计日志表';

create table if not exists patrol_control_person (
    control_id  varchar(64)  not null comment '布控ID',
    tenant_id   varchar(20)  default '000000' comment '租户编号',
    name        varchar(64)  not null comment '姓名',
    category    varchar(64)  default null comment '布控类别',
    id_card_no  varchar(64)  default null comment '身份证号',
    risk_level  varchar(32)  not null comment '风险等级',
    status      varchar(32)  not null comment '布控状态',
    source      varchar(120) default null comment '数据来源',
    expires_at  datetime     default null comment '到期时间',
    remark      varchar(500) default null comment '备注',
    face_image_url    varchar(500) default null comment '人脸底库图片地址',
    face_image_sha256 varchar(64)  default null comment '人脸底库图片SHA-256',
    face_updated_at   datetime     default null comment '人脸底库更新时间',
    create_dept bigint(20)   default null comment '创建部门',
    create_by   bigint(20)   default null comment '创建者',
    create_time datetime     default null comment '创建时间',
    update_by   bigint(20)   default null comment '更新者',
    update_time datetime     default null comment '更新时间',
    del_flag    char(1)      default '0' comment '删除标志',
    primary key (control_id),
    key idx_patrol_control_person_status (tenant_id, status),
    key idx_patrol_control_person_name (tenant_id, name)
) engine=innodb default charset=utf8mb4 comment='人员布控表';

create table if not exists patrol_control_vehicle (
    control_id   varchar(64)  not null comment '布控ID',
    tenant_id    varchar(20)  default '000000' comment '租户编号',
    plate_no     varchar(32)  not null comment '车牌号',
    vehicle_desc varchar(120) default null comment '车辆描述',
    vehicle_type varchar(32)  default null comment '车辆类型',
    risk_level   varchar(32)  not null comment '风险等级',
    status       varchar(32)  not null comment '布控状态',
    source       varchar(120) default null comment '数据来源',
    expires_at   datetime     default null comment '到期时间',
    remark       varchar(500) default null comment '备注',
    create_dept  bigint(20)   default null comment '创建部门',
    create_by    bigint(20)   default null comment '创建者',
    create_time  datetime     default null comment '创建时间',
    update_by    bigint(20)   default null comment '更新者',
    update_time  datetime     default null comment '更新时间',
    del_flag     char(1)      default '0' comment '删除标志',
    primary key (control_id),
    key idx_patrol_control_vehicle_status (tenant_id, status),
    key idx_patrol_control_vehicle_plate (tenant_id, plate_no)
) engine=innodb default charset=utf8mb4 comment='车辆布控表';

create table if not exists patrol_device_config (
    config_id                  varchar(64)  not null comment '配置ID',
    tenant_id                  varchar(20)  default '000000' comment '租户编号',
    device_id                  varchar(64)  not null comment '设备ID',
    supports_glasses           tinyint(1)   default 0 comment '是否支持眼镜能力',
    supports_earphone          tinyint(1)   default 0 comment '是否支持耳机能力',
    supports_wifi              tinyint(1)   default 0 comment '是否支持Wi-Fi',
    supports_file_transfer     tinyint(1)   default 0 comment '是否支持文件传输',
    supports_photo             tinyint(1)   default 0 comment '是否支持拍照',
    supports_video             tinyint(1)   default 0 comment '是否支持视频',
    supports_audio_record      tinyint(1)   default 0 comment '是否支持录音',
    supports_realtime_audio    tinyint(1)   default 0 comment '是否支持实时音频',
    wifi_enabled               tinyint(1)   default 0 comment 'Wi-Fi是否启用',
    wifi_ssid                  varchar(128) default null comment 'Wi-Fi SSID',
    wifi_password_configured   tinyint(1)   default 0 comment '是否已配置Wi-Fi密码',
    wifi_connected             tinyint(1)   default 0 comment 'Wi-Fi是否已连接',
    video_width                int          default 240 comment '视频宽度',
    video_height               int          default 0 comment '视频高度',
    video_frame_rate           int          default 16 comment '视频帧率',
    recording_duration_seconds int          default 86400 comment '录制时长秒数',
    vertical_recording         tinyint(1)   default 1 comment '是否竖屏录制',
    enhanced_sound             tinyint(1)   default 1 comment '是否增强音效',
    brightness_level           int          default 2 comment '亮度档位',
    realtime_audio_syncing     tinyint(1)   default 0 comment '实时音频同步状态',
    last_media_sync_at         datetime     default null comment '最近媒体同步完成时间',
    create_dept                bigint(20)   default null comment '创建部门',
    create_by                  bigint(20)   default null comment '创建者',
    create_time                datetime     default null comment '创建时间',
    update_by                  bigint(20)   default null comment '更新者',
    update_time                datetime     default null comment '更新时间',
    del_flag                   char(1)      default '0' comment '删除标志',
    primary key (config_id),
    unique key uk_patrol_device_config_device (tenant_id, device_id)
) engine=innodb default charset=utf8mb4 comment='设备能力与高级配置表';

create table if not exists patrol_cerebellum_config (
    config_id   varchar(64)   not null comment '配置ID',
    tenant_id   varchar(20)   default '000000' comment '租户编号',
    user_id     bigint(20)    not null comment '用户ID',
    user_name   varchar(64)   default null comment '用户姓名',
    badge_no    varchar(64)   default null comment '警号',
    base_url    varchar(500)  default null comment '小脑服务地址',
    api_key     varchar(255)  default null comment '小脑API Key',
    create_dept bigint(20)    default null comment '创建部门',
    create_by   bigint(20)    default null comment '创建者',
    create_time datetime      default null comment '创建时间',
    update_by   bigint(20)    default null comment '更新者',
    update_time datetime      default null comment '更新时间',
    del_flag    char(1)       default '0' comment '删除标志',
    primary key (config_id),
    unique key uk_patrol_cerebellum_user (tenant_id, user_id)
) engine=innodb default charset=utf8mb4 comment='用户小脑连接配置表';

create table if not exists patrol_app_version (
    version_id   varchar(64)  not null comment '版本ID',
    tenant_id    varchar(20)  default '000000' comment '租户编号',
    version_code int          not null comment '版本号编码',
    version_name varchar(64)  not null comment '版本名称',
    force_update tinyint(1)   default 0 comment '是否强制更新',
    changelog    text         default null comment '更新日志',
    download_url varchar(500) default null comment '下载地址',
    sha256       varchar(128) default null comment '安装包SHA-256',
    file_id      varchar(64)  default null comment '关联文件ID',
    status       varchar(32)  not null comment '状态',
    published_at datetime     default null comment '发布时间',
    create_dept  bigint(20)   default null comment '创建部门',
    create_by    bigint(20)   default null comment '创建者',
    create_time  datetime     default null comment '创建时间',
    update_by    bigint(20)   default null comment '更新者',
    update_time  datetime     default null comment '更新时间',
    del_flag     char(1)      default '0' comment '删除标志',
    primary key (version_id),
    key idx_patrol_app_version_status (tenant_id, status, version_code)
) engine=innodb default charset=utf8mb4 comment='App版本表';

create table if not exists patrol_firmware_version (
    firmware_id         varchar(64)  not null comment '固件ID',
    tenant_id           varchar(20)  default '000000' comment '租户编号',
    device_type         varchar(32)  not null comment '设备类型：HEADSET/GLASSES',
    vendor              varchar(64)  default null comment '厂商',
    chipset             varchar(64)  default null comment '芯片平台：ACTS/JL',
    device_model        varchar(100) default null comment '设备型号',
    hardware_version    varchar(64)  default null comment '硬件版本',
    firmware_type       varchar(32)  default 'FULL' comment '固件类型',
    version_code        int          not null comment '排序版本号',
    version_name        varchar(64)  not null comment '固件版本名称',
    min_current_version varchar(64)  default null comment '最低可升级当前版本',
    max_current_version varchar(64)  default null comment '最高可升级当前版本',
    force_update        tinyint(1)   default 0 comment '是否强制升级',
    changelog           text         default null comment '更新日志',
    download_url        varchar(500) default null comment '下载地址',
    sha256              varchar(128) default null comment '固件包SHA-256',
    file_id             varchar(64)  default null comment '关联文件ID',
    file_size_bytes     bigint       default 0 comment '文件大小',
    package_format      varchar(32)  default null comment '包格式：bin/zip/ufw',
    upgrade_mode        varchar(32)  default 'APP_BLE' comment '升级模式',
    gray_scope          varchar(32)  default 'ALL' comment '灰度范围',
    gray_targets        text         default null comment '灰度目标',
    status              varchar(32)  not null comment '状态',
    published_at        datetime     default null comment '发布时间',
    remark              varchar(500) default null comment '备注',
    create_dept         bigint(20)   default null comment '创建部门',
    create_by           bigint(20)   default null comment '创建者',
    create_time         datetime     default null comment '创建时间',
    update_by           bigint(20)   default null comment '更新者',
    update_time         datetime     default null comment '更新时间',
    del_flag            char(1)      default '0' comment '删除标志',
    primary key (firmware_id),
    key idx_patrol_firmware_match (tenant_id, status, device_type, chipset, version_code),
    key idx_patrol_firmware_file (file_id)
) engine=innodb default charset=utf8mb4 comment='设备固件版本表';

create table if not exists patrol_firmware_upgrade_task (
    task_id       varchar(64)  not null comment '升级任务ID',
    tenant_id     varchar(20)  default '000000' comment '租户编号',
    device_id     varchar(64)  not null comment '设备ID',
    firmware_id   varchar(64)  not null comment '固件ID',
    operator_id   varchar(64)  default null comment '操作人',
    from_version  varchar(64)  default null comment '原固件版本',
    to_version    varchar(64)  default null comment '目标固件版本',
    status        varchar(32)  not null comment '状态',
    progress      decimal(6,3) default 0 comment '进度',
    error_code    varchar(64)  default null comment '错误码',
    error_message varchar(500) default null comment '错误信息',
    started_at    datetime     default null comment '开始时间',
    finished_at   datetime     default null comment '完成时间',
    create_dept   bigint(20)   default null comment '创建部门',
    create_by     bigint(20)   default null comment '创建者',
    create_time   datetime     default null comment '创建时间',
    update_by     bigint(20)   default null comment '更新者',
    update_time   datetime     default null comment '更新时间',
    del_flag      char(1)      default '0' comment '删除标志',
    primary key (task_id),
    key idx_patrol_firmware_task_device (tenant_id, device_id, started_at),
    key idx_patrol_firmware_task_status (tenant_id, status)
) engine=innodb default charset=utf8mb4 comment='设备固件升级任务表';

insert ignore into sys_user values(9527, '000000', 103, 'POLICE_9527', '张警官', 'sys_user', 'zhang.police@city.gov.cn', '13800009527', '0', null, '$2a$10$X7Dwu6JiORKduaP8iS9sOOgUk/w93X63gAg2XAGGAXRs0KbiaNSki', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '移动端巡逻警员');
insert ignore into sys_user_role values ('9527', '3');
insert ignore into sys_user_post values ('9527', '4');

insert ignore into patrol_media (
    media_id, tenant_id, file_id, file_name, media_type, captured_at, size_text, file_size_bytes, mime_type,
    duration_text, sha256_verified, storage_side, transfer_status, progress, content_uri, bucket_name, object_key,
    sha256, watermark_token, badge_no, officer_name, device_id, biz_type, biz_id, evidence_source,
    create_dept, create_by, create_time, del_flag
) values
    (95270001, '000000', 'SEED-ZHANG-VIDEO-20260515', '张警官_商业街巡逻视频_20260515.mp4', 'VIDEO', '09:18:32', '37.8 KB', 38695, 'video/mp4',
     '00:03', 1, 'PHONE', 'DONE', 1, '/files/SEED-ZHANG-VIDEO-20260515/download', 'patrol-samples', 'classpath:patrol-samples/zhang-duty-video.mp4',
     '987a0de57d266af646a014f8b4e77c964ccfbf5fdf07858e8ff6ad766579e827', 'wm-seed-zhang-video-20260515', 'POLICE_9527', '张警官', 'DEV-GLASS-9527', 'DAILY_REPORT', 'MISSION-POLICE_9527-20260515', 'SEED_SAMPLE',
     103, 9527, sysdate(), '0'),
    (95270002, '000000', 'SEED-ZHANG-AUDIO-20260515', '张警官_现场询问录音_20260515.wav', 'AUDIO', '10:42:08', '344.6 KB', 352878, 'audio/wav',
     '00:04', 1, 'PHONE', 'DONE', 1, '/files/SEED-ZHANG-AUDIO-20260515/download', 'patrol-samples', 'classpath:patrol-samples/zhang-duty-audio.wav',
     '444757312b89160cefae137c2e95571ba26211b03ec58def3756a03837942765', 'wm-seed-zhang-audio-20260515', 'POLICE_9527', '张警官', 'DEV-GLASS-9527', 'DAILY_REPORT', 'MISSION-POLICE_9527-20260515', 'SEED_SAMPLE',
     103, 9527, sysdate(), '0'),
    (95270003, '000000', 'SEED-ZHANG-PHOTO-20260515', '张警官_重点点位照片_20260515.jpg', 'PHOTO', '14:06:25', '19.3 KB', 19736, 'image/jpeg',
     null, 1, 'PHONE', 'DONE', 1, '/files/SEED-ZHANG-PHOTO-20260515/download', 'patrol-samples', 'classpath:patrol-samples/zhang-duty-photo.jpg',
     'fe3036243614d7cd3148b42e6637e618910714d931a5d2d5c120ea4c63bc8105', 'wm-seed-zhang-photo-20260515', 'POLICE_9527', '张警官', 'DEV-GLASS-9527', 'DAILY_REPORT', 'MISSION-POLICE_9527-20260515', 'SEED_SAMPLE',
     103, 9527, sysdate(), '0');
