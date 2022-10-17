CREATE TABLE IF NOT EXISTS USERS
(
    user_id int NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_name varchar(255) NOT NULL,
    user_email varchar(255) NOT NULL,
    user_login varchar(255) NOT NULL,
    user_birthday date NOT NULL,
    UNIQUE (user_email),
    UNIQUE (user_login)
    );

CREATE TABLE IF NOT EXISTS FRIENDS
(
    user_id int NOT NULL REFERENCES USERS (user_id),
    friend_id int NOT NULL REFERENCES USERS (user_id),
    PRIMARY KEY (user_id, friend_id)
    );

CREATE TABLE IF NOT EXISTS MPA
(
    mpa_id int NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mpa_rating varchar(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS FILMS
(
    film_id int NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    film_name varchar(255) NOT NULL,
    film_description varchar(200) NOT NULL,
    film_release_date date NOT NULL,
    film_duration int NOT NULL,
    film_rate int NOT NULL,
    mpa_id int NOT NULL,
    FOREIGN KEY (mpa_id) REFERENCES MPA (mpa_id)
    );

CREATE TABLE IF NOT EXISTS GENRES
(
    genre_id int NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    genre varchar(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS FILM_GENRES
(
    film_id int NOT NULL REFERENCES FILMS (film_id),
    genre_id int NOT NULL REFERENCES GENRES (genre_id),
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS LIKES
(
    film_id int NOT NULL REFERENCES FILMS (film_id),
    user_id int NOT NULL REFERENCES USERS (user_id),
    PRIMARY KEY (film_id, user_id)
    );