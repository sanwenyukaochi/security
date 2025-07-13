create table sys_tenants
(
    status     boolean      not null,
    created_at bigint,
    created_by bigint,
    id         bigint       not null,
    updated_at bigint,
    updated_by bigint,
    code       varchar(50)  not null,
    name       varchar(100) not null,
    primary key (),
    unique (),
    constraint uk_tenant_name
        unique ()
);

comment on table sys_tenants is '租户表';

comment on column sys_tenants.status is '状态（true=启用，false=禁用）';

comment on column sys_tenants.created_at is '创建时间';

comment on column sys_tenants.created_by is '创建者';

comment on column sys_tenants.id is '主键ID';

comment on column sys_tenants.updated_at is '更新时间';

comment on column sys_tenants.updated_by is '更新者';

comment on column sys_tenants.code is '租户编码，唯一';

comment on column sys_tenants.name is '租户名称';

alter table sys_tenants
    owner to postgres;

create unique index sys_tenants_pkey
    on sys_tenants (id);

create unique index sys_tenants_code_key
    on sys_tenants (code);

create unique index uk_tenant_name
    on sys_tenants (name);

INSERT INTO public.sys_tenants (status, created_at, created_by, id, updated_at, updated_by, code, name) VALUES (true, 1752419096003, 1944412733822865408, 1944412733822865409, 1752419096003, 1944412733822865408, 'system_group', '系统管理员组');
