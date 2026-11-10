package dataaccess.sql;

import server.*;
import dataaccess.*;
import service.*;
import chess.ChessGame;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class sqlaccess {
	private static DataAccess da;
	private static List<UserData> users;
	private static List<String> auths;

	@BeforeAll
	public static void init() {
		da = new SQLAccess();
		clear();
		users = new ArrayList<>();
		auths = new ArrayList<>();
	}

	private static void clear() {
		da.clearUsers();
		da.clearAuth();
		da.clearGames();
	}

	/*
	 * private String formatRequest(Map<String, Object> map) {
		 * return serializer.toJson(map);
	 * }
	 */

	@Test
	@Order(2)
	public void addFindOfUserWorks() {
		UserData john = new UserData("john", "johnspass", "john@johnsawesome.com");
		da.addUser(john);
		UserData found = da.findUser(john.username());
		assertEquals(john.username(), found.username());
		assertEquals(john.email(), found.email());
		UserData bob = new UserData("bob", "fih4aWGNbqeoigaa12ea", "b0b@hotmail.com");
		da.addUser(bob);
		found = da.findUser(bob.username());
		assertEquals(bob.username(), found.username());
		assertEquals(bob.email(), found.email());
		users.add(john);
		users.add(bob);
	}

	@Test
	@Order(3)
	public void authWorks() {
		auths.add("jo32h1n15-1432-41341-32");
		auths.add("bob-3124-155-132019432");
		AuthData johnsauth = new AuthData("john", auths.get(0));
		da.addAuth(johnsauth);
		AuthData bobsauth = new AuthData("bob", auths.get(1));
		da.addAuth(bobsauth);
		AuthData found = da.findAuth(johnsauth.authToken());
		assertNotNull(found);
		if (null != found) {
			assertEquals(johnsauth.authToken(), found.authToken());
		}
		found = da.findAuth(bobsauth.authToken());
		assertNotNull(found);
		if (null != found) {
			assertEquals(bobsauth.authToken(), found.authToken());
		}
	}

	@Test
	@Order(4)
	public void gameStuffWorks() {
		ChessGame gameObject = new ChessGame();
		GameData game = new GameData(1, "null", "null", "cucumbers", gameObject);
		da.addGame(game);
		game = new GameData(1, "bob", "joe", "cucumbers", gameObject);
		da.overwriteGame(game);
		GameData found = da.findGame(1);
		assertEquals(found, game);
		Collection<GameData> games = da.getGames();
		assertNotNull(games);
		assertEquals(games.size(), 1);
		assertEquals(((ArrayList<GameData>) games).getFirst(), game);
	}

	@Test
	@Order(5)
	public void deleteAuthWorks() {
		String tok = auths.get(0);
		da.deleteAuth(tok);
		assertNull(da.findAuth(tok));
		assertNotNull(da.findUser("john"));
	}

	@Test
	@Order(6)
	public void clearWorks() {
		clear();
		assertEquals(da.getGames().size(), 0);
		assertNull(da.findAuth(auths.get(1)));
		assertNull(da.findUser("john"));
		assertNull(da.findUser("bob"));
	}
}
