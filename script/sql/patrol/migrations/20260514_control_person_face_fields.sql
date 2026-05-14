-- Existing database migration for PLBackend -> PLCerebellum face library sync.
-- Fresh deployments already include these columns in 05-patrol.sql.

set @schema_name = database();

set @sql = (
    select if(
        count(*) = 0,
        'alter table patrol_control_person add column face_image_url varchar(500) default null comment ''人脸底库图片地址'' after remark',
        'select ''face_image_url exists'''
    )
    from information_schema.columns
    where table_schema = @schema_name
      and table_name = 'patrol_control_person'
      and column_name = 'face_image_url'
);
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;

set @sql = (
    select if(
        count(*) = 0,
        'alter table patrol_control_person add column face_image_sha256 varchar(64) default null comment ''人脸底库图片SHA-256'' after face_image_url',
        'select ''face_image_sha256 exists'''
    )
    from information_schema.columns
    where table_schema = @schema_name
      and table_name = 'patrol_control_person'
      and column_name = 'face_image_sha256'
);
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;

set @sql = (
    select if(
        count(*) = 0,
        'alter table patrol_control_person add column face_updated_at datetime default null comment ''人脸底库更新时间'' after face_image_sha256',
        'select ''face_updated_at exists'''
    )
    from information_schema.columns
    where table_schema = @schema_name
      and table_name = 'patrol_control_person'
      and column_name = 'face_updated_at'
);
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
