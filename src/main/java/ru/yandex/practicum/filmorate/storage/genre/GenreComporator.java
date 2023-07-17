package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;

@Service
public class GenreComporator implements Comparator<Genre> {
    @Override
    public int compare(Genre o1, Genre o2) {
        return o1.getId()-o2.getId();
    }

}
