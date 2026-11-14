package client;

import org.junit.jupiter.api.*;
import server.Server;

import service.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static ServerFacade sf;
    private static Server server;
    private static AuthData auth;

    @BeforeAll
    public static void init() {
	server = new Server();
        var port = server.run(0);
	sf = new ServerFacade(port);
        System.out.println("Started test HTTP sf on " + port);
	sf.clear();
    }

    @Test
    @Order(1)
    public void usersWorks() {
	    auth = sf.registerUser(List.of("admin", "abc", "admin@chess.com"));
	    assertNotNull(auth);
	    AuthData auth2 = sf.login(List.of("admin", "abc"));
	    assertNotNull(auth2);
	    assertTrue(sf.logout(auth.authToken()));
	    auth = auth2;
    }

    @Test
    @Order(2)
    public void gamesWorks() {
	    GameData game = sf.createGame(List.of(auth.authToken(), "fluffy-bunny"));
	    sf.createGame(List.of(auth.authToken(), "lizardy"));
	    assertNotNull(game);
	    GameData game2 = sf.joinGame(List.of(auth.authToken(), "WHITE", "1"));
	    assertNotNull(game2);
	    assertFalse(game.equals(game2));
	    assertTrue(2 == sf.listGames(auth.authToken()).size());
	    assertEquals(game2, sf.getGame(game2.gameID()));
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
