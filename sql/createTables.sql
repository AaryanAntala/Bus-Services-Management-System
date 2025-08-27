-- this file contains the SQL commands to create the tables in the database

create table meals (
    meal_id int not null,
    meal_name varchar(50) not null,
    meal_category ENUM('veg', 'non-veg') not null,
    meal_price decimal(10,2) not null,
    stock int not null
);

create table orders (
    order_id int not null,
    bus_id int not null ,
    passenger_id int not null,
    seat_id int not null,
    meal_id int ,
    quantity int default 0,
    linen boolean default false,
    status varchar(30) default 'pending'
);

create table linen (
    bus_id int not null,
    linen_stock int not null
);

create table seats (
    bus_id int not null,
    seat_id int not null,
    booking_status ENUM('booked', 'available') default 'available'
);

create table booking (
    bus_id int not null,
    passenger_id int not null,
    seat_id int not null
);