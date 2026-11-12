package client;

import org.junit.jupiter.api.*;
import server.Server;

import service.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static ServerFacade server;
    private static AuthData auth;

    @BeforeAll
    public static void init() {
        server = new ServerFacade();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
	server.clear();
    }

    @Test
    @Order(1)
    public void usersWorks() {
	    auth = server.registerUser(List.of("admin", "abc", "admin@chess.com"));
	    assertNotNull(auth);
	    AuthData auth2 = server.login(List.of("admin", "abc"));
	    assertNotNull(auth2);
	    assertTrue(server.logout(auth.authToken()));
	    auth = auth2;
    }

    @Test
    @Order(2)
    public void gamesWorks() {
	    GameData game = server.createGame(List.of(auth.authToken(), "fluffy-bunny"));
	    server.createGame(List.of(auth.authToken(), "lizardy"));
	    assertNotNull(game);
	    GameData game2 = server.joinGame(List.of(auth.authToken(), "WHITE", "1"));
	    assertNotNull(game2);
	    assertFalse(game.equals(game2));
	    assertTrue(2 == server.listGames(auth.authToken()).size());
	    assertEquals(game2, server.getGame(game2.gameID()));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
