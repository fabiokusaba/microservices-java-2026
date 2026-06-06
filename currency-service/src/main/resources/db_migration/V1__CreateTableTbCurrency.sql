create table tb_currency (
    id serial primary key,
    source_currency char(3) not null,
    target_currency char(3) not null,
    conversion_rate decimal(10,2) not null
);