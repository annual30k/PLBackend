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

insert ignore into sys_user values(9527, '000000', 103, 'POLICE_9527', '张警官', 'sys_user', 'zhang.police@city.gov.cn', '13800009527', '0', null, '$2a$10$X7Dwu6JiORKduaP8iS9sOOgUk/w93X63gAg2XAGGAXRs0KbiaNSki', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '移动端巡逻警员');
insert ignore into sys_user_role values ('9527', '3');
insert ignore into sys_user_post values ('9527', '4');

insert ignore into patrol_device(device_id, tenant_id, device_name, device_type, service_uuid, mac_address, bonded, online, battery_percent, signal_bars, online_duration, storage_used_gb, storage_total_gb, firmware_version, recording_status, talking, cloud_connected, latitude, longitude, address, last_heartbeat_time, create_dept, create_by, create_time, del_flag)
values
('HEADSET_001', '000000', 'ForceLink-H1', 'HEADSET', '0000-pl2-ble-control', '2C:4A:91:3F:8B:02', 1, 1, 88, 4, '02:45:12', 42.5, 128, 'v1.2.4', 'IDLE', 0, 1, 26.1002000, 119.3065500, '福州温泉公园', sysdate(), 103, 1, sysdate(), '0'),
('RECORDER_A5', '000000', 'ForceLink-A5', 'RECORDER', '0000-pl2-ble-control', '4F:02:8C:76:A1:19', 0, 0, 72, 3, '00:00:00', 12.1, 64, 'v1.0.9', 'IDLE', 0, 0, 26.1005800, 119.3077100, '福州温泉公园东门', null, 103, 1, sysdate(), '0'),
('SENSOR_S9', '000000', 'ForceLink-S9', 'SENSOR', '0000-pl2-ble-control', '1E:BD:55:0A:44:71', 0, 0, 65, 2, '00:00:00', 2.4, 16, 'v1.1.1', 'IDLE', 0, 0, 26.1015500, 119.3090000, '核心商务区 CBD-North', null, 103, 1, sysdate(), '0'),
('GLASSES_G1', '000000', 'ForceLink-G1', 'GLASSES', '0000-pl2-ble-control', '6B:13:9E:41:D7:50', 0, 0, 91, 4, '00:00:00', 24.8, 128, 'v1.3.0', 'IDLE', 0, 0, 26.1025500, 119.3079500, '北侧周界入口', null, 103, 1, sysdate(), '0');

insert ignore into patrol_device_config(config_id, tenant_id, device_id, supports_glasses, supports_earphone, supports_wifi, supports_file_transfer, supports_photo, supports_video, supports_audio_record, supports_realtime_audio, wifi_enabled, wifi_ssid, wifi_password_configured, wifi_connected, video_width, video_height, video_frame_rate, recording_duration_seconds, vertical_recording, enhanced_sound, brightness_level, realtime_audio_syncing, create_dept, create_by, create_time, del_flag)
values
('CFG-HEADSET-001', '000000', 'HEADSET_001', 0, 1, 0, 1, 1, 1, 1, 1, 0, '', 0, 0, 240, 0, 16, 86400, 1, 1, 2, 0, 103, 1, sysdate(), '0'),
('CFG-GLASSES-001', '000000', 'GLASSES_G1', 1, 0, 1, 1, 1, 1, 0, 0, 1, 'PatrolLink-Device', 0, 1, 240, 0, 16, 86400, 1, 1, 2, 0, 103, 1, sysdate(), '0');

insert ignore into patrol_app_version(version_id, tenant_id, version_code, version_name, force_update, changelog, download_url, sha256, file_id, status, published_at, create_dept, create_by, create_time, del_flag)
values
('VER-ANDROID-125', '000000', 2, '1.2.5', 0, '对接平台端媒体上传\n对接设备高级能力与版本检查\n优化巡检消息与告警闭环', 'https://example.test/patrollink/PatrolLink-1.2.5.apk', null, null, 'PUBLISHED', sysdate(), 103, 1, sysdate(), '0');

insert ignore into patrol_device_binding(binding_id, tenant_id, device_id, user_id, user_name, nick_name, dept_id, dept_name, badge_no, bind_status, bound_at, remark, create_dept, create_by, create_time, del_flag)
values
('BIND-SEED-001', '000000', 'HEADSET_001', 9527, 'POLICE_9527', '张警官', 103, '巡逻组 A-42', 'POLICE_9527', 'BOUND', sysdate(), '初始化绑定', 103, 1, sysdate(), '0');
update patrol_device_binding set dept_name = '巡逻组 A-42' where binding_id = 'BIND-SEED-001';

insert ignore into patrol_alert(alert_id, tenant_id, title, level, status, occurred_at, location_text, source, description, confidence, create_dept, create_by, create_time, del_flag)
values
('AL-99824-03', '000000', '非法侵入监测', 'CRITICAL', 'PENDING', '14:32', '西三区 4号围墙 节点B', 'CAM-042', '围墙节点 B 检测到人员越界，耳机端已同步 12 秒现场视频片段。', '98.4%', 103, 1, sysdate(), '0'),
('AL-99824-04', '000000', '未识别车辆靠近', 'WARNING', 'PENDING', '14:38', '北侧周界入口', 'RFID-09', '车牌识别失败，建议现场复核并记录车辆去向。', '91.2%', 103, 1, sysdate(), '0'),
('AL-99821-11', '000000', '夜间巡查异常声源', 'INFO', 'CLOSED', '13:22', '核心商务区 CBD-North', 'HEADSET_001', '环境音频超过阈值，现场确认无风险。', '74.8%', 103, 1, sysdate(), '0');

insert ignore into patrol_alert_attachment(attachment_id, tenant_id, alert_id, client_file_id, file_name, mime_type, size_bytes, source, local_uri, upload_intent, create_dept, create_by, create_time, del_flag)
values
('ATT-SEED-001', '000000', 'AL-99821-11', 'AUD-318', '现场确认录音.m4a', 'audio/mp4', 9017753, 'AUDIO', 'device://AUD-318', 'ALERT_CLOSE', 103, 1, sysdate(), '0');

insert ignore into patrol_alert_disposition(disposition_id, tenant_id, alert_id, action_type, action_result, operator_id, operator_name, note, attachments_count, occurred_at, create_dept, create_by, create_time, del_flag)
values
('AD-SEED-001', '000000', 'AL-99821-11', 'CLOSE', 'RESOLVED', 'admin', 'admin', '现场确认无风险，已闭环。', 1, date_sub(sysdate(), interval 1 hour), 103, 1, sysdate(), '0');

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

insert ignore into patrol_location_track(track_id, tenant_id, badge_no, officer_name, device_id, latitude, longitude, accuracy_meters, address, reported_at, create_dept, create_by, create_time, del_flag)
values
('TRK-SEED-001', '000000', 'POLICE_9527', '张警官', 'HEADSET_001', 26.0987500, 119.3049500, 8.5, '福州温泉公园巡区入口', date_sub(sysdate(), interval 24 minute), 103, 1, sysdate(), '0'),
('TRK-SEED-002', '000000', 'POLICE_9527', '张警官', 'HEADSET_001', 26.1002000, 119.3065500, 7.2, '福州温泉公园', date_sub(sysdate(), interval 16 minute), 103, 1, sysdate(), '0'),
('TRK-SEED-003', '000000', 'POLICE_9527', '张警官', 'HEADSET_001', 26.1005800, 119.3077100, 6.9, '福州温泉公园东门', date_sub(sysdate(), interval 8 minute), 103, 1, sysdate(), '0');

insert ignore into patrol_device_command(command_id, tenant_id, device_id, command, operator_id, request_id, status, result_message, sent_at, ack_at, create_dept, create_by, create_time, del_flag)
values
('CMD-SEED-001', '000000', 'HEADSET_001', 'START_RECORD', 'admin', 'seed-001', 'ACKED', '端侧已进入录制状态', date_sub(sysdate(), interval 18 minute), date_sub(sysdate(), interval 17 minute), 103, 1, sysdate(), '0'),
('CMD-SEED-002', '000000', 'HEADSET_001', 'TAKE_PHOTO', 'admin', 'seed-002', 'ACCEPTED', '指令已写入，等待端侧回执', date_sub(sysdate(), interval 5 minute), null, 103, 1, sysdate(), '0');

insert ignore into patrol_device_event(event_id, tenant_id, device_id, event_type, event_level, event_title, event_detail, occurred_at, create_dept, create_by, create_time, del_flag)
values
('EVT-SEED-001', '000000', 'HEADSET_001', 'BIND', 'INFO', '设备绑定上线', 'HEADSET_001 已完成绑定并保持云端连接', date_sub(sysdate(), interval 22 minute), 103, 1, sysdate(), '0'),
('EVT-SEED-002', '000000', 'HEADSET_001', 'COMMAND', 'INFO', '平台下发设备指令', 'START_RECORD', date_sub(sysdate(), interval 18 minute), 103, 1, sysdate(), '0'),
('EVT-SEED-003', '000000', 'HEADSET_001', 'COMMAND', 'INFO', '平台下发设备指令', 'TAKE_PHOTO', date_sub(sysdate(), interval 5 minute), 103, 1, sysdate(), '0');

insert ignore into patrol_message(message_id, tenant_id, title, content, target_type, target_id, target_name, channel, status, read_count, total_count, sent_at, create_dept, create_by, create_time, del_flag)
values
('MSG-001', '000000', '现场支援', '请前往温泉公园北侧入口支援未识别车辆复核。', 'SINGLE', 'POLICE_9527', '张警官', 'APP', 'READ', 1, 1, date_sub(sysdate(), interval 30 minute), 103, 1, sysdate(), '0'),
('MSG-002', '000000', '重点预警升级', '重点人员预警升级，注意联动盘查并上传现场照片。', 'ORG', 'TEAM-A-42', '巡逻组 A-42', 'APP', 'SENT', 0, 1, date_sub(sysdate(), interval 18 minute), 103, 1, sysdate(), '0'),
('MSG-003', '000000', '设备电量提醒', '设备低电量，请更换备用设备并保持心跳在线。', 'DEVICE', 'HEADSET_001', 'ForceLink-H1', 'APP', 'SENT', 0, 1, date_sub(sysdate(), interval 9 minute), 103, 1, sysdate(), '0');

insert ignore into patrol_message_receipt(receipt_id, tenant_id, message_id, recipient_id, recipient_name, device_id, delivery_status, delivered_at, read_at, last_pull_at, create_dept, create_by, create_time, del_flag)
values
('MR-001', '000000', 'MSG-001', 'POLICE_9527', '张警官', 'HEADSET_001', 'READ', date_sub(sysdate(), interval 29 minute), date_sub(sysdate(), interval 28 minute), date_sub(sysdate(), interval 29 minute), 103, 1, sysdate(), '0'),
('MR-002', '000000', 'MSG-002', 'POLICE_9527', '张警官', 'HEADSET_001', 'PENDING', null, null, null, 103, 1, sysdate(), '0'),
('MR-003', '000000', 'MSG-003', 'POLICE_9527', '张警官', 'HEADSET_001', 'PENDING', null, null, null, 103, 1, sysdate(), '0');

insert ignore into patrol_control_person(control_id, tenant_id, name, category, id_card_no, risk_level, status, source, expires_at, remark, create_dept, create_by, create_time, del_flag)
values
('CP-001', '000000', '李某某', '重点关注', null, 'HIGH', 'ENABLED', '第三方重点人员库', '2026-06-30 23:59:59', '人脸比对命中后转预警', 103, 1, sysdate(), '0'),
('CP-002', '000000', '王某某', '临控人员', null, 'MEDIUM', 'ENABLED', '平台导入', '2026-05-31 23:59:59', '巡区临时布控', 103, 1, sysdate(), '0');

insert ignore into patrol_control_vehicle(control_id, tenant_id, plate_no, vehicle_desc, vehicle_type, risk_level, status, source, expires_at, remark, create_dept, create_by, create_time, del_flag)
values
('CV-001', '000000', '京A12345', '黑色 SUV', 'SUV', 'HIGH', 'ENABLED', '第三方重点车辆库', '2026-06-30 23:59:59', '车牌 OCR 命中后转预警', 103, 1, sysdate(), '0'),
('CV-002', '000000', '京B67890', '白色轿车', 'SEDAN', 'MEDIUM', 'DISABLED', '平台录入', '2026-05-31 23:59:59', '人工登记车辆', 103, 1, sysdate(), '0');

insert ignore into patrol_audit_log(log_id, tenant_id, log_type, operator_name, action, resource, result, ip_address, trace_id, occurred_at, create_dept, create_by, create_time, del_flag)
values
('AUD-001', '000000', 'COMMAND', 'admin', '下发录制指令', 'HEADSET_001', 'SUCCESS', '127.0.0.1', 'seed-command-001', date_sub(sysdate(), interval 18 minute), 103, 1, sysdate(), '0'),
('AUD-002', '000000', 'ALERT', 'admin', '确认预警', 'AL-99824-04', 'SUCCESS', '127.0.0.1', 'seed-alert-001', date_sub(sysdate(), interval 12 minute), 103, 1, sysdate(), '0'),
('AUD-003', '000000', 'MESSAGE', 'admin', '发送指挥消息', 'MSG-002', 'SUCCESS', '127.0.0.1', 'seed-message-001', date_sub(sysdate(), interval 9 minute), 103, 1, sysdate(), '0');
