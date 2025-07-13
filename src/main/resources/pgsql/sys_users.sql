create table sys_users
(
    account_non_expired     boolean      not null,
    account_non_locked      boolean      not null,
    credentials_non_expired boolean      not null,
    status                  boolean      not null,
    created_at              bigint,
    created_by              bigint,
    id                      bigint       not null,
    tenant                  bigint       not null
        constraint fk_user_tenant
            references ??? (),
    updated_at              bigint,
    updated_by              bigint,
    phone                   varchar(20)  not null,
    user_name               varchar(50)  not null,
    email                   varchar(100) not null,
    password_hash           varchar(120) not null,
    tenant_id               varchar(255) not null,
    primary key (),
    constraint uk_user_username
        unique ()
);

comment on table sys_users is '用户表';

comment on column sys_users.account_non_expired is '账户是否未过期（true=有效，false=过期）';

comment on column sys_users.account_non_locked is '账户是否未锁定（true=正常，false=锁定）';

comment on column sys_users.credentials_non_expired is '密码是否未过期（true=有效，false=已过期）';

comment on column sys_users.status is '状态（true=启用，false=禁用）';

comment on column sys_users.created_at is '创建时间';

comment on column sys_users.created_by is '创建者';

comment on column sys_users.id is '主键ID';

comment on column sys_users.tenant is '租户信息';

comment on column sys_users.updated_at is '更新时间';

comment on column sys_users.updated_by is '更新者';

comment on column sys_users.phone is '手机号';

comment on column sys_users.user_name is '用户名';

comment on column sys_users.email is '邮箱';

comment on column sys_users.password_hash is '用户密码';

comment on column sys_users.tenant_id is '租户ID';

alter table sys_users
    owner to postgres;

create unique index sys_users_pkey
    on sys_users (id);

create unique index uk_user_username
    on sys_users (user_name, tenant_id);

INSERT INTO public.sys_users (account_non_expired, account_non_locked, credentials_non_expired, status, created_at, created_by, id, tenant, updated_at, updated_by, phone, user_name, email, password_hash, tenant_id) VALUES (true, true, true, true, 1752419096157, 1944412734665920512, 1944412734665920512, 1944412733822865409, 1752419096157, 1944412734665920512, '13800138001', 'admin', 'admin@example.com', '$2a$10$T3tT..puNrrcoFNutp5vieMKBQ.sUyKjdB3jppaBhTYck8p3bU7ou', '1944412733822865409');
INSERT INTO public.sys_users (account_non_expired, account_non_locked, credentials_non_expired, status, created_at, created_by, id, tenant, updated_at, updated_by, phone, user_name, email, password_hash, tenant_id) VALUES (true, true, true, true, 1752419096259, 1944412734665920512, 1944412735303454720, 1944412733822865409, 1752419096259, 1944412734665920512, '13800138002', 'tenant', 'tenant@example.com', '$2a$10$pDWbBExezQSKAmL/4u7AF.FDlFkLPXdure2BbfuGjKVbFCclt1uLq', '1944412733822865409');
INSERT INTO public.sys_users (account_non_expired, account_non_locked, credentials_non_expired, status, created_at, created_by, id, tenant, updated_at, updated_by, phone, user_name, email, password_hash, tenant_id) VALUES (true, true, true, true, 1752419096350, 1944412734665920512, 1944412735647387648, 1944412733822865409, 1752419096350, 1944412734665920512, '13800138003', 'user', 'user@example.com', '$2a$10$EeXKe/OUqcSxHT42KivD.unuELY/xKst85zIhB9Qnxk9lps4voDue', '1944412733822865409');
