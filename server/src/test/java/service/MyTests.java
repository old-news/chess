package service;

import server.*;
import dataaccess.*;
import chess.ChessGame;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyTests {
	private static Handler hand;
	private static Gson serializer;
	private static String currAuth;
	private static int currID;

	@BeforeAll
	public static void init() {
		hand = new Handler();
		serializer = new Gson();
	}

	private String formatRequest(Map<String, Object> map) {
		return serializer.toJson(map);
	}

	@Test
	public void ramaccessWorks() {
		DataAccess da = new RamAccess();
		UserData user = new UserData("admin", "pass", "cool@hot.com");
		da.addUser(user);
		assertNotNull(da.findUser(user.username()));

		String authToken = "1234";
		AuthData auth = new AuthData(user.username(), authToken);
		da.addAuth(auth);
		assertNotNull(da.findAuth(authToken));

		int gameID = 42;
		ChessGame gameObject = new ChessGame();
		GameData game = new GameData(gameID, user.username(), null, "mygame", gameObject);
		da.addGame(game);
		assertNotNull(da.findGame(gameID));

        da.deleteAuth(authToken);
        assertNull(da.findAuth(authToken));
	}

	@Test
	@Order(1)
	public void registerWorks() {
		String req = formatRequest(Map.of("username", "admin", "password", "abc", "email", "supercool@this.com"));
		HandlerResult res = hand.registerUser(req);
		assertEquals(200, res.status());
		assertNotNull(res.body().get("username"));
		assertNotNull(res.body().get("authToken"));
        hand.logout((String) res.body().get("authToken"));
	}

	@Test
	@Order(2)
	public void loginWorks() {
		String req = formatRequest(Map.of("username", "admin", "password", "abc"));
		HandlerResult res = hand.login(req);
		assertEquals(200, res.status());
		System.out.println(res.body());
		assertNotNull(res.body().get("authToken"));
		currAuth = (String) res.body().get("authToken");
	}

	@Test
	@Order(3)
	public void logoutWorks() {
		HandlerResult res = hand.logout(currAuth);
		assertEquals(200, res.status());
		assertEquals("true", (String) res.body().get("success"));
	}

	@Test
	@Order(4)
	public void createWorks() {
		String req = formatRequest(Map.of("username", "admin", "password", "abc"));
		HandlerResult res = hand.login(req);
		currAuth = (String) res.body().get("authToken");
		req = formatRequest(Map.of("gameName", "pickles"));
		res = hand.createGame(currAuth, req);
		assertEquals(200, res.status());
		assertNotNull(res.body().get("game"));
		GameData game = (GameData) res.body().get("game");
		assertNotNull(game.whiteUsername());
		currID = game.gameID();
	}

	@Test
	@Order(5)
	public void listWorks() {
		HandlerResult res = hand.listGames(currAuth);
		assertEquals(200, res.status());
		assertNotNull(res.body().get("games"));
	}

	@Test
	@Order(6)
	public void joinWorks() {
		String req = formatRequest(Map.of("playerColor", "black", "gameID", currID));
		HandlerResult res = hand.joinGame(currAuth, req);
		assertEquals(200, res.status());
		assertNotNull(res.body().get("game"));
		GameData game = (GameData) res.body().get("game");
		assertEquals("admin", game.blackUsername());
	}
}
