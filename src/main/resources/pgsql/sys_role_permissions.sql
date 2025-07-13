create table sys_role_permissions
(
    id            bigint       not null,
    permission_id bigint       not null
        constraint fk_role_permission_permission_id
            references ??? (),
    role_id       bigint       not null
        constraint fk_role_permission_role_id
            references ??? (),
    tenant_id     varchar(255) not null,
    primary key (),
    constraint uk_role_permission
        unique ()
);

comment on table sys_role_permissions is '角色权限关联表';

comment on column sys_role_permissions.id is '主键ID';

comment on column sys_role_permissions.permission_id is '权限ID';

comment on column sys_role_permissions.role_id is '角色ID';

comment on column sys_role_permissions.tenant_id is '租户ID';

alter table sys_role_permissions
    owner to postgres;

create unique index sys_role_permissions_pkey
    on sys_role_permissions (id);

create unique index uk_role_permission
    on sys_role_permissions (role_id, permission_id);

INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412736964399104, 1944412736314281984, 1944412736003903488, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737044090880, 1944412736444305408, 1944412736003903488, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737102811136, 1944412736515608576, 1944412736003903488, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737203474432, 1944412736591106048, 1944412736003903488, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737278971904, 1944412736679186432, 1944412736003903488, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737362857984, 1944412736444305408, 1944412736159092736, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737408995328, 1944412736591106048, 1944412736159092736, '1944412733822865409');
INSERT INTO public.sys_role_permissions (id, permission_id, role_id, tenant_id) VALUES (1944412737463521280, 1944412736444305408, 1944412736242978816, '1944412733822865409');
