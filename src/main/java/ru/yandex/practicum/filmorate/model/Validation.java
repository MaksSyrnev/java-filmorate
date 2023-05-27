package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

public class Validation {
    public static final LocalDate START_FILM_DATE = LocalDate.of(1895,12,28);
    final static int LENGTH_FILM_DESCRIPTION = 200;

    public static boolean isLengthOk(String string) {
        if ((string == null) || string.isBlank()) {
            return true;
        }
        if (string.length() > LENGTH_FILM_DESCRIPTION) {
            return false;
        }
        return true;
    }

    public static boolean isDateFilmOk(LocalDate date) {
       if (date == null) {
           return true;
       }
        return START_FILM_DATE.isBefore(date);
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
