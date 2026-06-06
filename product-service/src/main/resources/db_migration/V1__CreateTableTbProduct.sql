create table tb_product (
    id serial primary key,
    description varchar(100) not null,
    brand varchar(255) not null,
    model varchar(255) not null,
    currency varchar(3) not null,
    price decimal(10,2) not null,
    stock integer not null
);