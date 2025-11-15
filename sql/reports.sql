create schema reports;

create sequence reports.sq_dat_report;

create table reports.dat_report (
    report_id bigint not null primary key,
    report_group varchar(255),
    report_name varchar(255)
);

comment on table reports.dat_report is 'Отчеты системы';

comment on column reports.dat_report.report_id is 'Идентификатор отчета';
comment on column reports.dat_report.report_group is 'Группа отчетов';
comment on column reports.dat_report.report_name is 'Название отчета';

insert into reports.dat_report(report_id, report_group, report_name)
values (nextval('reports.sq_dat_report'),
        'userlistview', 'Список пользователей');