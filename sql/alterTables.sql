-- this script is mean to add the primary key and foreign key constraints to the tables

alter table seats
    add constraint pk_seats primary key (bus_id, seat_id);

alter table meals
    add constraint pk_meals primary key (meal_id);

alter table linen
    add constraint pk_linen primary key (bus_id);

alter table booking
    add constraint pk_booking primary key (bus_id, seat_id),
    add constraint fk_booking foreign key (bus_id, seat_id) references seats(bus_id, seat_id)
    on update cascade;

alter table orders
    add constraint pk_orders primary key (order_id),
    modify column order_id int not null auto_increment,
    add constraint fk_orders_meals foreign key (meal_id) references meals(meal_id),
    add constraint fk_orders_booking foreign key (bus_id, seat_id) references booking(bus_id, seat_id)
    on update cascade;