CREATE TABLE IF NOT EXISTS film_genre (
  genre_id INT PRIMARY KEY AUTO_INCREMENT,
  genre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_rating (
  rating_id INT PRIMARY KEY AUTO_INCREMENT,
  rating VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
  film_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  release_date DATE NOT NULL,
  duration INT NOT NULL CHECK (duration >= 0),
  rating_id INT NOT NULL,
  CONSTRAINT fk_films_rating FOREIGN KEY (rating_id) REFERENCES film_rating (rating_id)
);

CREATE TABLE IF NOT EXISTS users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  login VARCHAR(100) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INT NOT NULL,
  genre_id INT NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  CONSTRAINT fk_film_genres_film FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
  CONSTRAINT fk_film_genres_genre FOREIGN KEY (genre_id) REFERENCES film_genre (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_like (
  user_id INT NOT NULL,
  film_id INT NOT NULL,
  PRIMARY KEY (user_id, film_id),
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
  CONSTRAINT fk_like_film FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
  user_id INT NOT NULL,
  friend_id INT NOT NULL,
  PRIMARY KEY (user_id, friend_id),
  CONSTRAINT fk_friends_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
  CONSTRAINT fk_friends_friend FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
  CONSTRAINT chk_no_self_friend CHECK (user_id <> friend_id)
);