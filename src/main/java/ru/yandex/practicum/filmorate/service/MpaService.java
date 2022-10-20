package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> findAll() {
        log.debug("MpaService: запрос на получение всех рейтингов.");
        return mpaDbStorage.findAll();
    }

    public Mpa get(Integer mpaId) {
        log.debug("MpaService: запрос на получение рейтинга с id: {}.", mpaId);
        validateDataExists(mpaId);
        return mpaDbStorage.get(mpaId);
    }

    public void validateDataExists(Integer mpaId) {
        log.debug("MpaService: запрос на проверку наличия рейтинга с id: {} в БД.", mpaId);
        if (!mpaDbStorage.validateDataExists(mpaId)) {
            String message = "Рейтинга c таким id не существует.";
            throw new MpaDoesNotExistException(message);
        }
    }
}
