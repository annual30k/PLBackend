set @patrol_media_metadata_exists = (
    select count(*)
    from information_schema.columns
    where table_schema = database()
      and table_name = 'patrol_media'
      and column_name = 'metadata_json'
);

set @patrol_media_metadata_sql = if(
    @patrol_media_metadata_exists = 0,
    'alter table patrol_media add column metadata_json varchar(1000) default null comment ''文件业务元数据JSON'' after evidence_source',
    'select 1'
);

prepare patrol_media_metadata_stmt from @patrol_media_metadata_sql;
execute patrol_media_metadata_stmt;
deallocate prepare patrol_media_metadata_stmt;
