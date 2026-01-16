import models

public class DateAndTime {
    public static void dateInput(int year, int month, int day){
        LocalDate date = LocalDate.of(year, month, day);
    }

    public static void timeInput(int hour, int minute){
        LocalTime time = LocalTime.of(hour, minute);
    }
}
