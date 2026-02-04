CREATE TABLE roles(
    id        BIGSERIAL primary key,
    authority varchar(255) not null,
    constraint roles_authority_uk unique (authority)
);

CREATE TABLE users(
    id       BIGSERIAL primary key,
    username varchar(255) not null,
    password varchar(255) not null,
    constraint users_username_uk unique (username)
);

CREATE TABLE user_roles(
    user_id BIGINT not null,
    role_id BIGINT not null,
    primary key (user_id, role_id),
    constraint user_roles_user_fk foreign key (user_id) references users (id),
    constraint user_roles_role_fk foreign key (role_id) references roles (id)
);

CREATE TABLE tokens(
    id            BIGSERIAL primary key,
    token_session varchar(255) not null,
    deleted_at    timestamp    not null,
    user_id       BIGINT       not null,
    constraint tokens_user_fk foreign key (user_id) references users (id),
    constraint token_token_session_uk unique (token_session)
);
