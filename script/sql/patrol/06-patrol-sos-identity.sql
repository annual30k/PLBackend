-- Existing MySQL deployments: complete the SOS operator/device snapshot fields.
drop procedure if exists add_patrol_sos_column_if_absent;
delimiter //
create procedure add_patrol_sos_column_if_absent(in p_column varchar(64), in p_definition text)
begin
    if not exists (
        select 1 from information_schema.columns
        where table_schema = database()
          and table_name = 'patrol_sos_event'
          and column_name = p_column
    ) then
        set @ddl = concat('alter table patrol_sos_event add column ', p_column, ' ', p_definition);
        prepare stmt from @ddl;
        execute stmt;
        deallocate prepare stmt;
    end if;
end//
delimiter ;
call add_patrol_sos_column_if_absent('user_id', 'bigint(20) default null comment ''发起用户ID''');
call add_patrol_sos_column_if_absent('officer_name', 'varchar(120) default null comment ''发起警员姓名''');
call add_patrol_sos_column_if_absent('badge_no', 'varchar(64) default null comment ''发起警员警号''');
call add_patrol_sos_column_if_absent('dept_name', 'varchar(120) default null comment ''发起警员部门''');
call add_patrol_sos_column_if_absent('device_id', 'varchar(64) default null comment ''发起设备ID''');
drop procedure if exists add_patrol_sos_column_if_absent;
