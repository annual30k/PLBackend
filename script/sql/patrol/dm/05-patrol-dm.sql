-- PatrolLink 业务表兼容脚本。
-- 基础 RuoYi 表请先执行 script/sql/postgres 或 script/sql/oracle 下对应框架脚本。
-- 本脚本只包含 patrol_* 业务表 DDL，不包含演示种子数据。

create table patrol_device (
    device_id           varchar(64)    not null,
    tenant_id           varchar(20)    default '000000',
    device_name         varchar(100)   not null,
    device_type         varchar(32)    default 'HEADSET',
    service_uuid        varchar(100),
    mac_address         varchar(64),
    bonded              smallint     default 0,
    online              smallint     default 0,
    battery_percent     int            default 0,
    signal_bars         int            default 0,
    online_duration     varchar(32)    default '00:00:00',
    storage_used_gb     numeric(10,2)  default 0,
    storage_total_gb    numeric(10,2)  default 0,
    firmware_version    varchar(32),
    recording_status    varchar(32)    default 'IDLE',
    talking             smallint     default 0,
    cloud_connected     smallint     default 0,
    latitude            numeric(10,7),
    longitude           numeric(10,7),
    address             varchar(255),
    last_heartbeat_time timestamp,
    create_dept         bigint,
    create_by           bigint,
    create_time         timestamp,
    update_by           bigint,
    update_time         timestamp,
    del_flag            char(1)        default '0',
    primary key (device_id)
);

create table patrol_alert (
    alert_id       varchar(64)   not null,
    tenant_id      varchar(20)   default '000000',
    title          varchar(120)  not null,
    level          varchar(32)   not null,
    status         varchar(32)   not null,
    occurred_at    varchar(32),
    location_text  varchar(255),
    source         varchar(64),
    description    varchar(1000),
    confidence     varchar(32),
    close_result   varchar(64),
    close_note     varchar(1000),
    operator_id    varchar(64),
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp,
    del_flag       char(1)       default '0',
    primary key (alert_id)
);

create table patrol_alert_attachment (
    attachment_id  varchar(64)  not null,
    tenant_id      varchar(20)  default '000000',
    alert_id       varchar(64)  not null,
    client_file_id varchar(64),
    file_name      varchar(255),
    mime_type      varchar(100),
    size_bytes     bigint,
    source         varchar(32),
    local_uri      varchar(500),
    upload_intent  varchar(64),
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp,
    del_flag       char(1)      default '0',
    primary key (attachment_id)
);

create table patrol_alert_disposition (
    disposition_id    varchar(64)   not null,
    tenant_id         varchar(20)   default '000000',
    alert_id          varchar(64)   not null,
    action_type       varchar(32)   not null,
    action_result     varchar(64),
    operator_id       varchar(64),
    operator_name     varchar(64),
    note              varchar(1000),
    attachments_count int           default 0,
    occurred_at       timestamp,
    create_dept       bigint,
    create_by         bigint,
    create_time       timestamp,
    update_by         bigint,
    update_time       timestamp,
    del_flag          char(1)       default '0',
    primary key (disposition_id)
);

create table patrol_media (
    media_id          bigint    not null,
    tenant_id         varchar(20)   default '000000',
    file_id           varchar(64)   not null,
    file_name         varchar(255)  not null,
    media_type        varchar(32)   not null,
    captured_at       varchar(32),
    size_text         varchar(32),
    file_size_bytes   bigint,
    mime_type         varchar(128),
    duration_text     varchar(32),
    sha256_verified   smallint    default 0,
    storage_side      varchar(32)   not null,
    transfer_status   varchar(32)   default 'IDLE',
    progress          numeric(5,2)  default 0,
    content_uri       varchar(500),
    oss_id            bigint,
    bucket_name       varchar(100),
    object_key        varchar(500),
    sha256            varchar(128),
    watermark_token   varchar(128),
    badge_no          varchar(64),
    officer_name      varchar(120),
    device_id         varchar(64),
    biz_type          varchar(64),
    biz_id            varchar(64),
    evidence_source   varchar(64),
    create_dept       bigint,
    create_by         bigint,
    create_time       timestamp,
    update_by         bigint,
    update_time       timestamp,
    del_flag          char(1)       default '0',
    primary key (media_id)
);

create table patrol_media_upload_task (
    task_id           varchar(64)   not null,
    tenant_id         varchar(20)   default '000000',
    file_id           varchar(64),
    file_name         varchar(255)  not null,
    media_type        varchar(32),
    mime_type         varchar(128),
    file_size_bytes   bigint    default 0,
    chunk_size_bytes  bigint    default 0,
    total_chunks      int           default 0,
    uploaded_chunks   int           default 0,
    uploaded_bytes    bigint    default 0,
    expected_sha256   varchar(128),
    actual_sha256     varchar(128),
    storage_side      varchar(32)   default 'PHONE',
    biz_type          varchar(64),
    biz_id            varchar(64),
    status            varchar(32)   default 'INIT',
    progress          numeric(5,4)  default 0,
    temp_dir          varchar(512),
    error_message     varchar(500),
    badge_no          varchar(64),
    officer_name      varchar(120),
    device_id         varchar(64),
    completed_at      timestamp,
    create_dept       bigint,
    create_by         bigint,
    create_time       timestamp,
    update_by         bigint,
    update_time       timestamp,
    del_flag          char(1)       default '0',
    primary key (task_id)
);

create table patrol_area (
    area_id       varchar(64)   not null,
    tenant_id     varchar(20)   default '000000',
    area_name     varchar(120)  not null,
    team_id       varchar(64),
    team_name     varchar(120),
    boundary_json clob,
    route_json    clob,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp,
    del_flag      char(1)       default '0',
    primary key (area_id)
);

create table patrol_sos_event (
    sos_id             varchar(64)   not null,
    tenant_id          varchar(20)   default '000000',
    phase              varchar(32)   not null,
    message            varchar(255),
    latitude           numeric(10,7),
    longitude          numeric(10,7),
    accuracy_meters    numeric(10,2),
    address            varchar(255),
    recording_audio    smallint    default 0,
    backup_eta_minutes int,
    create_dept        bigint,
    create_by          bigint,
    create_time        timestamp,
    update_by          bigint,
    update_time        timestamp,
    del_flag           char(1)       default '0',
    primary key (sos_id)
);

create table patrol_sos_disposition (
    disposition_id       varchar(64)   not null,
    tenant_id            varchar(20)   default '000000',
    sos_id               varchar(64)   not null,
    action_type          varchar(64)   not null,
    action_result        varchar(64),
    operator_id          varchar(64),
    operator_name        varchar(120),
    note                 varchar(500),
    contact_name         varchar(120),
    contact_phone        varchar(64),
    attachment_file_id   varchar(64),
    attachment_file_name varchar(255),
    backup_eta_minutes   int,
    occurred_at          timestamp,
    create_dept          bigint,
    create_by            bigint,
    create_time          timestamp,
    update_by            bigint,
    update_time          timestamp,
    del_flag             char(1)       default '0',
    primary key (disposition_id)
);

create table patrol_location_track (
    track_id        varchar(64)   not null,
    tenant_id       varchar(20)   default '000000',
    badge_no        varchar(64),
    officer_name    varchar(64),
    device_id       varchar(64)   not null,
    latitude        numeric(10,7) not null,
    longitude       numeric(10,7) not null,
    accuracy_meters numeric(10,2),
    address         varchar(255),
    reported_at     timestamp,
    create_dept     bigint,
    create_by       bigint,
    create_time     timestamp,
    update_by       bigint,
    update_time     timestamp,
    del_flag        char(1)       default '0',
    primary key (track_id)
);

create table patrol_device_command (
    command_id     varchar(64)   not null,
    tenant_id      varchar(20)   default '000000',
    device_id      varchar(64)   not null,
    command        varchar(64)   not null,
    operator_id    varchar(64),
    request_id     varchar(128),
    status         varchar(32)   not null,
    result_message varchar(500),
    sent_at        timestamp,
    ack_at         timestamp,
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp,
    del_flag       char(1)       default '0',
    primary key (command_id)
);

create table patrol_device_event (
    event_id     varchar(64)   not null,
    tenant_id    varchar(20)   default '000000',
    device_id    varchar(64)   not null,
    event_type   varchar(64)   not null,
    event_level  varchar(32)   not null,
    event_title  varchar(120)  not null,
    event_detail varchar(1000),
    occurred_at  timestamp,
    create_dept  bigint,
    create_by    bigint,
    create_time  timestamp,
    update_by    bigint,
    update_time  timestamp,
    del_flag     char(1)       default '0',
    primary key (event_id)
);

create table patrol_device_binding (
    binding_id  varchar(64)  not null,
    tenant_id   varchar(20)  default '000000',
    device_id   varchar(64)  not null,
    user_id     bigint,
    user_name   varchar(64),
    nick_name   varchar(64),
    dept_id     bigint,
    dept_name   varchar(120),
    badge_no    varchar(64),
    bind_status varchar(32)  not null,
    bound_at    timestamp,
    unbound_at  timestamp,
    remark      varchar(500),
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp,
    del_flag    char(1)      default '0',
    primary key (binding_id)
);

create table patrol_message (
    message_id  varchar(64)   not null,
    tenant_id   varchar(20)   default '000000',
    title       varchar(120)  not null,
    content     varchar(1000) not null,
    target_type varchar(32)   not null,
    target_id   varchar(64)   not null,
    target_name varchar(120),
    channel     varchar(32)   default 'APP',
    status      varchar(32)   not null,
    read_count  int           default 0,
    total_count int           default 1,
    sent_at     timestamp,
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp,
    del_flag    char(1)       default '0',
    primary key (message_id)
);

create table patrol_message_receipt (
    receipt_id      varchar(64)  not null,
    tenant_id       varchar(20)  default '000000',
    message_id      varchar(64)  not null,
    recipient_id    varchar(64)  not null,
    recipient_name  varchar(120),
    device_id       varchar(64),
    delivery_status varchar(32)  not null,
    delivered_at    timestamp,
    read_at         timestamp,
    last_pull_at    timestamp,
    create_dept     bigint,
    create_by       bigint,
    create_time     timestamp,
    update_by       bigint,
    update_time     timestamp,
    del_flag        char(1)      default '0',
    primary key (receipt_id)
);

create table patrol_audit_log (
    log_id        varchar(64)  not null,
    tenant_id     varchar(20)  default '000000',
    log_type      varchar(32)  not null,
    operator_name varchar(64),
    action        varchar(120) not null,
    resource      varchar(128),
    result        varchar(32)  not null,
    ip_address    varchar(64),
    trace_id      varchar(128),
    occurred_at   timestamp,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp,
    del_flag      char(1)      default '0',
    primary key (log_id)
);

create table patrol_control_person (
    control_id  varchar(64)  not null,
    tenant_id   varchar(20)  default '000000',
    name        varchar(64)  not null,
    category    varchar(64),
    id_card_no  varchar(64),
    risk_level  varchar(32)  not null,
    status      varchar(32)  not null,
    source      varchar(120),
    expires_at  timestamp,
    remark      varchar(500),
    face_image_url    varchar(500),
    face_image_sha256 varchar(64),
    face_updated_at   timestamp,
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp,
    del_flag    char(1)      default '0',
    primary key (control_id)
);

create table patrol_control_vehicle (
    control_id   varchar(64)  not null,
    tenant_id    varchar(20)  default '000000',
    plate_no     varchar(32)  not null,
    vehicle_desc varchar(120),
    vehicle_type varchar(32),
    risk_level   varchar(32)  not null,
    status       varchar(32)  not null,
    source       varchar(120),
    expires_at   timestamp,
    remark       varchar(500),
    create_dept  bigint,
    create_by    bigint,
    create_time  timestamp,
    update_by    bigint,
    update_time  timestamp,
    del_flag     char(1)      default '0',
    primary key (control_id)
);

create table patrol_device_config (
    config_id                  varchar(64)  not null,
    tenant_id                  varchar(20)  default '000000',
    device_id                  varchar(64)  not null,
    supports_glasses           smallint   default 0,
    supports_earphone          smallint   default 0,
    supports_wifi              smallint   default 0,
    supports_file_transfer     smallint   default 0,
    supports_photo             smallint   default 0,
    supports_video             smallint   default 0,
    supports_audio_record      smallint   default 0,
    supports_realtime_audio    smallint   default 0,
    wifi_enabled               smallint   default 0,
    wifi_ssid                  varchar(128),
    wifi_password_configured   smallint   default 0,
    wifi_connected             smallint   default 0,
    video_width                int          default 240,
    video_height               int          default 0,
    video_frame_rate           int          default 16,
    recording_duration_seconds int          default 86400,
    vertical_recording         smallint   default 1,
    enhanced_sound             smallint   default 1,
    brightness_level           int          default 2,
    realtime_audio_syncing     smallint   default 0,
    last_media_sync_at         timestamp,
    create_dept                bigint,
    create_by                  bigint,
    create_time                timestamp,
    update_by                  bigint,
    update_time                timestamp,
    del_flag                   char(1)      default '0',
    primary key (config_id)
);

create table patrol_app_version (
    version_id   varchar(64)  not null,
    tenant_id    varchar(20)  default '000000',
    version_code int          not null,
    version_name varchar(64)  not null,
    force_update smallint   default 0,
    changelog    clob,
    download_url varchar(500),
    sha256       varchar(128),
    file_id      varchar(64),
    status       varchar(32)  not null,
    published_at timestamp,
    create_dept  bigint,
    create_by    bigint,
    create_time  timestamp,
    update_by    bigint,
    update_time  timestamp,
    del_flag     char(1)      default '0',
    primary key (version_id)
);

create index idx_patrol_device_tenant_online on patrol_device (tenant_id, online);
create index idx_patrol_alert_tenant_status on patrol_alert (tenant_id, status);
create index idx_patrol_alert_attachment_alert on patrol_alert_attachment (tenant_id, alert_id);
create index idx_patrol_alert_disposition_alert on patrol_alert_disposition (tenant_id, alert_id, occurred_at);
create unique index uk_patrol_media_file_side on patrol_media (tenant_id, file_id, storage_side);
create index idx_patrol_media_tenant_side on patrol_media (tenant_id, storage_side);
create index idx_patrol_upload_task_status on patrol_media_upload_task (tenant_id, status);
create index idx_patrol_upload_task_file on patrol_media_upload_task (tenant_id, file_id);
create index idx_patrol_sos_tenant_phase on patrol_sos_event (tenant_id, phase);
create index idx_patrol_sos_disposition_sos on patrol_sos_disposition (tenant_id, sos_id, occurred_at);
create index idx_patrol_track_badge_time on patrol_location_track (tenant_id, badge_no, reported_at);
create index idx_patrol_track_device_time on patrol_location_track (tenant_id, device_id, reported_at);
create index idx_patrol_command_device_time on patrol_device_command (tenant_id, device_id, sent_at);
create index idx_patrol_command_status on patrol_device_command (tenant_id, status);
create index idx_patrol_device_event_device_time on patrol_device_event (tenant_id, device_id, occurred_at);
create index idx_patrol_device_event_type on patrol_device_event (tenant_id, event_type);
create index idx_patrol_device_binding_device on patrol_device_binding (tenant_id, device_id, bind_status);
create index idx_patrol_device_binding_user on patrol_device_binding (tenant_id, user_name, bind_status);
create index idx_patrol_message_target on patrol_message (tenant_id, target_type, target_id);
create index idx_patrol_message_time on patrol_message (tenant_id, sent_at);
create unique index uk_patrol_message_receipt_user on patrol_message_receipt (tenant_id, message_id, recipient_id);
create index idx_patrol_message_receipt_message on patrol_message_receipt (tenant_id, message_id);
create index idx_patrol_message_receipt_target on patrol_message_receipt (tenant_id, recipient_id, device_id, delivery_status);
create index idx_patrol_audit_time on patrol_audit_log (tenant_id, occurred_at);
create index idx_patrol_audit_resource on patrol_audit_log (tenant_id, resource);
create index idx_patrol_control_person_status on patrol_control_person (tenant_id, status);
create index idx_patrol_control_person_name on patrol_control_person (tenant_id, name);
create index idx_patrol_control_vehicle_status on patrol_control_vehicle (tenant_id, status);
create index idx_patrol_control_vehicle_plate on patrol_control_vehicle (tenant_id, plate_no);
create unique index uk_patrol_device_config_device on patrol_device_config (tenant_id, device_id);
create index idx_patrol_app_version_status on patrol_app_version (tenant_id, status, version_code);
