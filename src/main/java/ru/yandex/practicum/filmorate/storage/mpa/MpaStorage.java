package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Mpa addMpa(Mpa mpa);

    Optional<Mpa> updateMpa(Mpa mpa);

    Optional<Mpa> getMpaById(int id);

    List<Mpa> getAllMpa();

    int deleteMpaById(int id);

    int deleteAllMpa();
}
