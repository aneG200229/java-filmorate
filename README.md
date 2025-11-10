# java-filmorate
Template repository for Filmorate project.
## Схема базы данных

![Database Schema](/docs/database_schema.png)

### Описание таблиц

#### 1. **users** - пользователи
- `user_id` (PK) - уникальный идентификатор
- `email` - электронная почта
- `login` - логин пользователя
- `name` - имя пользователя
- `birthday` - дата рождения

#### 2. **films** - фильмы
- `film_id` (PK) - уникальный идентификатор
- `name` - название фильма
- `description` - описание
- `release_date` - дата релиза
- `duration` - продолжительность в минутах
- `rating_id` (FK) - рейтинг фильма (связь с `film_rating`)
- `genre_id` (FK) - жанр фильма (связь с `film_genre`)

#### 3. **film_rating** - рейтинги MPA
- `rating_id` (PK) - уникальный идентификатор
- `rating` - название рейтинга (G, PG, PG-13, R, NC-17)

#### 4. **film_genre** - жанры фильмов
- `genre_id` (PK) - уникальный идентификатор
- `genre` - название жанра (Комедия, Драма, Боевик и т.д.)

#### 5. **user_like** - лайки пользователей к фильмам
- `film_id` (FK) - идентификатор фильма
- `user_id` (FK) - идентификатор пользователя
- Композитный первичный ключ `(film_id, user_id)`

#### 6. **friends** - дружба между пользователями
- `user_id` (FK) - идентификатор пользователя
- `friend_id` (FK) - идентификатор друга
- Композитный первичный ключ `(user_id, friend_id)`

