create table sys_user_roles
(
    id        bigint       not null,
    role_id   bigint       not null
        constraint fk_user_role_role_id
            references ??? (),
    user_id   bigint       not null
        constraint fk_user_role_user_id
            references ??? (),
    tenant_id varchar(255) not null,
    primary key (),
    constraint uk_user_role
        unique ()
);

comment on table sys_user_roles is '用户角色关联表';

comment on column sys_user_roles.id is '主键ID';

comment on column sys_user_roles.role_id is '角色ID';

comment on column sys_user_roles.user_id is '用户ID';

comment on column sys_user_roles.tenant_id is '租户ID';

alter table sys_user_roles
    owner to postgres;

create unique index sys_user_roles_pkey
    on sys_user_roles (id);

create unique index uk_user_role
    on sys_user_roles (user_id, role_id);

INSERT INTO public.sys_user_roles (id, role_id, user_id, tenant_id) VALUES (1944412736746295296, 1944412736003903488, 1944412734665920512, '1944412733822865409');
INSERT INTO public.sys_user_roles (id, role_id, user_id, tenant_id) VALUES (1944412736821792768, 1944412736159092736, 1944412735303454720, '1944412733822865409');
INSERT INTO public.sys_user_roles (id, role_id, user_id, tenant_id) VALUES (1944412736905678848, 1944412736242978816, 1944412735647387648, '1944412733822865409');
