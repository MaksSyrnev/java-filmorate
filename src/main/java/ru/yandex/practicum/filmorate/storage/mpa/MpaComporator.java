package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;

@Service
public class MpaComporator implements Comparator<Mpa> {
    @Override
    public int compare(Mpa o1, Mpa o2) {
        return o1.getId() - o2.getId();
    }

}
