package models;
import java.time.*;

public class DateAndTime {
    public LocalDate date;
    public LocalTime time;

    public DateAndTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public static LocalDate dateInput(int year, int month, int day){
        return LocalDate.of(year, month, day);
    }

    public static LocalTime timeInput(int hour, int minute){
        return LocalTime.of(hour, minute);
    }
}
