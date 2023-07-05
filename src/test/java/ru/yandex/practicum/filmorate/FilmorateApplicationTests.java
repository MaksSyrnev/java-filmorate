package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
	private UserController userController;
	private FilmController filmController;

	@Autowired
	public FilmorateApplicationTests(UserController userController, FilmController filmController) {
		this.userController = userController;
		this.filmController = filmController;
	}

	@BeforeEach
	public void beforeEach() {
		filmController.deleteAllFilms();
		userController.deleteAllUsers();
	}

	@Test
	@DisplayName("Проверка добавления пользователя с корректными данными")
	void validationCorrectUserDate() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		userController.addUser(user);
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(1, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("Добавление пользователя логин с пробелом")
	void validationFailUserLogin() {
		User user = new User();
		user.setLogin("Nick Name");
		user.setName("Nick Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		final ValidationException exception = assertThrows(
			ValidationException.class,
			new Executable() {
				@Override
				public void execute() {
					userController.addUser(user);
				}
			}
		);
		assertEquals("логин должен одно слово, не может быть пустым", exception.getMessage());
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(0, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("добавление пользователя пустой логин")
	void validationNullUserLogin() {
		User user = new User();
		user.setName("Nick Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						userController.addUser(user);
					}
				}
		);
		assertEquals("логин должен одно слово, не может быть пустым", exception.getMessage());
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(0, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("добавление пользователя пустое имя")
	void validationNullUserName() {
		User user = new User();
		user.setLogin("dolore");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		userController.addUser(user);
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(1, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
		assertEquals("dolore", savedUsers.get(0).getName(),
				"Имя не совпадает с логином пользователя");
	}

	@Test
	@DisplayName("Добавление пользователя некорректная почта")
	void validationFailUserMail() {
		User user = new User();
		user.setLogin("Nick Name");
		user.setName("Nick Name");
		user.setEmail("mailmail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						userController.addUser(user);
					}
				}
		);
		assertEquals("некоректные данные в почте", exception.getMessage());
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(0, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("Добавление пользователя пустая почта")
	void validationNullUserMail() {
		User user = new User();
		user.setLogin("Nick Name");
		user.setName("Nick Name");
		user.setBirthday(LocalDate.of(1946,8,20));
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						userController.addUser(user);
					}
				}
		);
		assertEquals("некоректные данные в почте", exception.getMessage());
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(0, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("добавление пользователя неверная дата рожения")
	void validationFailUserBirthday() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(2946,8,20));
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						userController.addUser(user);
					}
				}
		);
		assertEquals("дата рождения не может быть больше текущей даты", exception.getMessage());
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(0, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("добавление пользователя пустая дата рождения")
	void validationNullUserBirthday() {
		User user = new User();
		user.setLogin("dolore");
		user.setEmail("mail@mail.ru");
		userController.addUser(user);
		final List<User> savedUsers = userController.getUsers();
		assertNotNull(savedUsers,"Список пользователей не возвращается");
		assertEquals(1, savedUsers.size(),
				"Возвращается неверное количество пользователей.");
	}

	@Test
	@DisplayName("добавление фильма корректные данные")
	void validationFilmCorrectData() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		filmController.addFilm(film);
		final List<Film> savedFilms = filmController.getFilms();
		assertNotNull(savedFilms,"Список фильмов не возвращается");
		assertEquals(1, savedFilms.size(),
				"Возвращается неверное количество фильмов.");
	}

	@Test
	@DisplayName("добавление фильма пустое название")
	void validationFilmNullName() {
		Film film = new Film();
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		final ConstraintViolationException exception = assertThrows(
				ConstraintViolationException.class,
				new Executable() {
					@Override
					public void execute() {
						filmController.addFilm(film);
					}
				}
		);
		assertEquals("addFilm.film.name: must not be blank", exception.getMessage());
		final List<Film> savedFilms = filmController.getFilms();
		assertNotNull(savedFilms,"Список фильмов не возвращается");
		assertEquals(0, savedFilms.size(),
				"Возвращается неверное количество фильмов.");
	}

	@Test
	@DisplayName("добавление фильма все данные пустые кроме названия")
	void validationFilmNullData() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		filmController.addFilm(film);
		final List<Film> savedFilms = filmController.getFilms();
		assertNotNull(savedFilms,"Список фильмов не возвращается");
		assertEquals(1, savedFilms.size(),
				"Возвращается неверное количество фильмов.");
	}

	@Test
	@DisplayName("добавление фильма все дата релиза некорректная")
	void validationFilmIncorrectDateRelese() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1867,03,25));
		film.setDuration(100);
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						filmController.addFilm(film);
					}
				}
		);
		assertEquals("дата релиза некоректная", exception.getMessage());
		final List<Film> savedFilms = filmController.getFilms();
		assertNotNull(savedFilms,"Список фильмов не возвращается");
		assertEquals(0, savedFilms.size(),
				"Возвращается неверное количество фильмов.");
	}

	@Test
	@DisplayName("добавление фильма писание больше 200 символов")
	void validationFilmIncorrectDescription() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing rtirtuorturo " +
				"rturiotrtiro rturioturoituroti rturoturotiru rtiuroitrtoir" +
				"hgjkhalgjhf fhgjahfgajkfhgf aghjfa hgjkfgha ahgjkafhgkj ghfka ahgjhfgkfhgkljafhgkjfghakjghfakjfgha" +
				"hgjkhalgjhf fhgjahfgajkfhgf aghjfa hgjkfgha ahgjkafhgkj ghfka ahgjhfgkfhgkljafhgkjfghakjghfakjfgha" +
				"hgjkhalgjhf fhgjahfgajkfhgf aghjfa hgjkfgha ahgjkafhgkj ghfka ahgjhfgkfhgkljafhgkjfghakjghfakjfgha" +
				"hgjkhalgjhf fhgjahfgajkfhgf aghjfa hgjkfgha ahgjkafhgkj ghfka ahgjhfgkfhgkljafhgkjfghakjghfakjfgha" +
				"hgjkhalgjhf fhgjahfgajkfhgf aghjfa hgjkfgha ahgjkafhgkj ghfka ahgjhfgkfhgkljafhgkjfghakjghfakjfgha" +
				"hgjkhalgjhf fhgjahfgajkfhgf aghjfa hgjkfgha ahgjkafhgkj ghfka ahgjhfgkfhgkljafhgkjfghakjghfakjfgha");
		film.setReleaseDate(LocalDate.of(1997,03,25));
		film.setDuration(100);
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						filmController.addFilm(film);
					}
				}
		);
		assertEquals("слишком длинное описание фильма", exception.getMessage());
		final List<Film> savedFilms = filmController.getFilms();
		assertNotNull(savedFilms,"Список фильмов не возвращается");
		assertEquals(0, savedFilms.size(),
				"Возвращается неверное количество фильмов.");
	}

	@Test
	@DisplayName("добавление фильма продолжительность отрицательная")
	void validationFilmIncorrectDuration() {
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(-100);
		final ValidationException exception = assertThrows(
				ValidationException.class,
				new Executable() {
					@Override
					public void execute() {
						filmController.addFilm(film);
					}
				}
		);
		assertEquals("продолжительность фильма должна быть положительным числом", exception.getMessage());
		final List<Film> savedFilms = filmController.getFilms();
		assertNotNull(savedFilms,"Список фильмов не возвращается");
		assertEquals(0, savedFilms.size(),
				"Возвращается неверное количество фильмов.");
	}
}
