MERGE INTO film_rating (rating) KEY (rating) VALUES ('G');
MERGE INTO film_rating (rating) KEY (rating) VALUES ('PG');
MERGE INTO film_rating (rating) KEY (rating) VALUES ('PG-13');
MERGE INTO film_rating (rating) KEY (rating) VALUES ('R');
MERGE INTO film_rating (rating) KEY (rating) VALUES ('NC-17');

MERGE INTO film_genre (genre) KEY (genre) VALUES ('Комедия');
MERGE INTO film_genre (genre) KEY (genre) VALUES ('Драма');
MERGE INTO film_genre (genre) KEY (genre) VALUES ('Мультфильм');
MERGE INTO film_genre (genre) KEY (genre) VALUES ('Триллер');
MERGE INTO film_genre (genre) KEY (genre) VALUES ('Документальный');
MERGE INTO film_genre (genre) KEY (genre) VALUES ('Боевик');
