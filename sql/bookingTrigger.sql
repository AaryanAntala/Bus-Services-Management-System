DELIMITER $$

CREATE TRIGGER after_booking_insert
AFTER INSERT ON booking
FOR EACH ROW
BEGIN
    UPDATE seats
    SET booking_status = 'booked'
    WHERE bus_id = NEW.bus_id AND seat_id = NEW.seat_id;
END$$

DELIMITER ;