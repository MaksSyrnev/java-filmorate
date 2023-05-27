package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

public class Validation {

    public static boolean isLengthOk(String string) {
        final int lengthFilmDescription = 200;
        if ((string == null) || string.isBlank()) {
            return true;
        }
        if (string.length() > lengthFilmDescription) {
            return false;
        }
        return true;
    }

    public static boolean isDateFilmOk(LocalDate date) {
       final LocalDate startFilmDate = LocalDate.of(1895,12,28);
       if (date == null) {
           return true;
       }
        return startFilmDate.isBefore(date);
    }

    public static boolean isDateUserOk(LocalDate date) {
        LocalDate now = LocalDate.now();
        return now.isAfter(date);
    }

    public static boolean isHasEmailSymbol(String email) {
        return email.contains("@");
    }

    public static boolean isHasSpaceSymbol(String string) {
        if ((string == null) || string.isBlank()) {
            return true;
        }
        return string.contains(" ");
    }
}
