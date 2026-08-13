alter table patrol_alert
    add column latitude decimal(10,7) default null comment '预警纬度' after location_text,
    add column longitude decimal(10,7) default null comment '预警经度' after latitude,
    add column assigned_officer_name varchar(120) default null comment '指派警员' after operator_id,
    add column assigned_badge_no varchar(64) default null comment '指派警号' after assigned_officer_name,
    add column assigned_device_id varchar(64) default null comment '指派设备' after assigned_badge_no;

alter table patrol_sos_event
    add column assigned_officer_name varchar(120) default null comment '增援警员' after backup_eta_minutes,
    add column assigned_badge_no varchar(64) default null comment '增援警号' after assigned_officer_name,
    add column assigned_device_id varchar(64) default null comment '增援设备' after assigned_badge_no,
    add column received_at datetime default null comment '接警时间' after assigned_device_id,
    add column resolved_at datetime default null comment '完成时间' after received_at,
    add column resolution_result varchar(64) default null comment '处置结果' after resolved_at,
    add column resolution_note varchar(1000) default null comment '处置说明' after resolution_result;

update patrol_alert a
left join patrol_device d on d.tenant_id = a.tenant_id and d.device_id = a.source and d.del_flag = '0'
set a.latitude = coalesce(a.latitude, d.latitude),
    a.longitude = coalesce(a.longitude, d.longitude)
where a.latitude is null or a.longitude is null;
