create table sys_roles
(
    data_scope integer,
    status     boolean      not null,
    created_at bigint,
    created_by bigint,
    id         bigint       not null,
    tenant     bigint       not null
        constraint "FKbls65kxnqg7j84l63iv5vm8fg"
            references ??? (),
    updated_at bigint,
    updated_by bigint,
    code       varchar(100) not null,
    name       varchar(100) not null,
    tenant_id  varchar(255) not null,
    primary key (),
    constraint uk_role_code
        unique ()
);

comment on table sys_roles is '角色表';

comment on column sys_roles.data_scope is '数据权限（0=租户,1=本人,3=自定义）';

comment on column sys_roles.status is '状态（false禁用，true启用）';

comment on column sys_roles.created_at is '创建时间';

comment on column sys_roles.created_by is '创建者';

comment on column sys_roles.id is '主键ID';

comment on column sys_roles.tenant is '租户信息';

comment on column sys_roles.updated_at is '更新时间';

comment on column sys_roles.updated_by is '更新者';

comment on column sys_roles.code is '权限标识（如 sys:admin）';

comment on column sys_roles.name is '角色名称';

comment on column sys_roles.tenant_id is '租户ID';

alter table sys_roles
    owner to postgres;

create unique index sys_roles_pkey
    on sys_roles (id);

create unique index uk_role_code
    on sys_roles (code, tenant_id);

INSERT INTO public.sys_roles (data_scope, status, created_at, created_by, id, tenant, updated_at, updated_by, code, name, tenant_id) VALUES (0, true, 1752419096385, 1944412734665920512, 1944412736003903488, 1944412733822865409, 1752419096385, 1944412734665920512, 'admin', '系统管理员', '1944412733822865409');
INSERT INTO public.sys_roles (data_scope, status, created_at, created_by, id, tenant, updated_at, updated_by, code, name, tenant_id) VALUES (1, true, 1752419096410, 1944412734665920512, 1944412736159092736, 1944412733822865409, 1752419096410, 1944412734665920512, 'tenant', '租户管理员', '1944412733822865409');
INSERT INTO public.sys_roles (data_scope, status, created_at, created_by, id, tenant, updated_at, updated_by, code, name, tenant_id) VALUES (2, true, 1752419096433, 1944412734665920512, 1944412736242978816, 1944412733822865409, 1752419096433, 1944412734665920512, 'user', '普通用户', '1944412733822865409');
