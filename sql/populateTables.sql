-- this script will be used to intially populate the tables

insert into seats(bus_id, seat_id) values
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9), (3, 10);

insert into meals(meal_id, meal_name, meal_category, meal_price, meal_stock) values
(1, 'Chicken Biryani', 'non-veg', 150.00, 100),
(2, 'Paneer Cheese Sandwich', 'veg', 120.00, 50),
(3, 'Veg Pulao', 'veg', 100.00, 70),
(4, 'Samosa', 'veg', 30.00, 200),
(5, 'Vegetable Sandwich', 'veg',100.00, 50),
(6, 'Chicken Sandwich', 'non-veg',150.00, 30),
(7, 'Fruit Platter', 'veg',100.00, 25),
(8, 'Mixed Nuts', 'veg', 160.00, 100),
(9, 'Tea/Coffee', 'veg', 20.00, 200),
(10, 'Momos', 'veg', 60.00, 150);

insert into linen(bus_id, linen_stock) values
(1, 50),
(2, 30),
(3, 20);

insert into booking(bus_id, passenger_id, seat_id) values
(1, 101, 1),
(1, 102, 4),
(1, 103, 5),
(2, 104, 1),
(2, 105, 5),
(3, 106, 6),
(3, 107, 7),
(3, 108, 8),
(3, 109, 10),
(2, 110, 2),
(2 , 111, 9),
(2, 112, 7);

insert into orders(bus_id, passenger_id, seat_id, meal_id, quantity) values
(1, 101, 1, 1, 2),
(2, 105, 5, 5, 1),
(2 ,111 ,9 ,1 ,3),
(2 ,112 ,7 ,2 ,4), 
(3, 106, 6, 6, 2),
(3, 107, 7, 7, 3);