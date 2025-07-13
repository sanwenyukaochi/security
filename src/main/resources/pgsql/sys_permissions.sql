create table sys_permissions
(
    sort       integer,
    visible    boolean,
    created_at bigint,
    created_by bigint,
    id         bigint       not null,
    parent_id  bigint       not null,
    updated_at bigint,
    updated_by bigint,
    code       varchar(100) not null,
    name       varchar(100) not null,
    type       varchar(100) not null,
    path       varchar(255),
    tenant_id  varchar(255) not null,
    primary key (),
    constraint uk_permission_code
        unique ()
);

comment on table sys_permissions is '权限表';

comment on column sys_permissions.sort is '排序号';

comment on column sys_permissions.visible is '是否可见（true可见，false隐藏）';

comment on column sys_permissions.created_at is '创建时间';

comment on column sys_permissions.created_by is '创建者';

comment on column sys_permissions.id is '主键ID';

comment on column sys_permissions.parent_id is '父节点ID（0为根）';

comment on column sys_permissions.updated_at is '更新时间';

comment on column sys_permissions.updated_by is '更新者';

comment on column sys_permissions.code is '权限标识（如 user:add）';

comment on column sys_permissions.name is '权限名称';

comment on column sys_permissions.type is '类型（1菜单 2按钮）';

comment on column sys_permissions.path is '前端路由/按钮绑定路径';

comment on column sys_permissions.tenant_id is '租户ID';

alter table sys_permissions
    owner to postgres;

create unique index sys_permissions_pkey
    on sys_permissions (id);

create unique index uk_permission_code
    on sys_permissions (code);

INSERT INTO public.sys_permissions (sort, visible, created_at, created_by, id, parent_id, updated_at, updated_by, code, name, type, path, tenant_id) VALUES (1, true, 1752419096454, 1944412734665920512, 1944412736314281984, 1944412736314281985, 1752419096454, 1944412734665920512, 'user:manage', '用户管理', '1', '/user', '1944412733822865409');
INSERT INTO public.sys_permissions (sort, visible, created_at, created_by, id, parent_id, updated_at, updated_by, code, name, type, path, tenant_id) VALUES (1, true, 1752419096477, 1944412734665920512, 1944412736444305408, 1944412736314281984, 1752419096477, 1944412734665920512, 'user:view', '用户查看', '2', '/user/view', '1944412733822865409');
INSERT INTO public.sys_permissions (sort, visible, created_at, created_by, id, parent_id, updated_at, updated_by, code, name, type, path, tenant_id) VALUES (2, true, 1752419096495, 1944412734665920512, 1944412736515608576, 1944412736314281984, 1752419096495, 1944412734665920512, 'user:add', '用户新增', '2', '/user/add', '1944412733822865409');
INSERT INTO public.sys_permissions (sort, visible, created_at, created_by, id, parent_id, updated_at, updated_by, code, name, type, path, tenant_id) VALUES (3, true, 1752419096515, 1944412734665920512, 1944412736591106048, 1944412736314281984, 1752419096515, 1944412734665920512, 'user:edit', '用户编辑', '2', '/user/edit', '1944412733822865409');
INSERT INTO public.sys_permissions (sort, visible, created_at, created_by, id, parent_id, updated_at, updated_by, code, name, type, path, tenant_id) VALUES (4, true, 1752419096533, 1944412734665920512, 1944412736679186432, 1944412736314281984, 1752419096533, 1944412734665920512, 'user:delete', '用户删除', '2', '/user/delete', '1944412733822865409');
