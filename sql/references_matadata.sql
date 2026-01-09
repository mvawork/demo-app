create schema metadata;
alter schema metadata owner to testapp;

create table metadata.dic_reference
(
    reference_id bigint not null
        primary key,
    schema_name name not null,
    table_name  name not null,
    table_sql   text
);

comment on table metadata.dic_reference is 'Описание метатданных для работы со соправочниками';

comment on column metadata.dic_reference.reference_id is 'Идентификатор описания';

comment on column metadata.dic_reference.schema_name is 'Имя схемы';

comment on column metadata.dic_reference.table_name is 'Имя таблицы';

comment on column metadata.dic_reference.table_sql is 'SQL запрос';

alter table metadata.dic_reference owner to testapp;

create table metadata.dic_reference_field
(
    field_id    bigint not null
        primary key,
    reference_id bigint not null,
    field_name  varchar(255),
    field_title varchar(255),
    field_key   boolean,
    field_type  varchar(255),
    field_length integer,
    field_order integer,
    constraint fk_dic_reference_field_reference_id
        foreign key (reference_id)
        references metadata.dic_reference(reference_id)
);

comment on table metadata.dic_reference_field is 'Описание полей для справочников';

comment on column metadata.dic_reference_field.field_id is 'Идентификатор поля';

comment on column metadata.dic_reference_field.reference_id is 'Идентификатор описания';

comment on column metadata.dic_reference_field.field_name is 'Наименование поля';

comment on column metadata.dic_reference_field.field_key is 'Ключевое поле';

comment on column metadata.dic_reference_field.field_title is 'Заголовок поля';

comment on column metadata.dic_reference_field.field_type is 'Тип поля';

comment on column metadata.dic_reference_field.field_length is 'Длина поля';

comment on column metadata.dic_reference_field.field_order is 'Порядковый номер';

alter table metadata.dic_reference_field owner to testapp;

