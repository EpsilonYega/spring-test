create table t_task(
    id uuid primary key,
    c_details text check ( length(trim(c_details)) > 1),
    c_completed boolean not null default false
)