package ru.yandex.practicum.filmorate.exception;

public class LikeDoesNotExistException extends RuntimeException {

    public LikeDoesNotExistException(String message) {
        super(message);
    }
}
