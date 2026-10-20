package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;
    private final Service serve;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

	javalin.delete("/db", ctx -> clearDB(ctx));
	/*
	 * javalin.post("/user", ctx -> registerUser(ctx));
	 * javalin.post("/login", ctx -> login(ctx));
	 * javalin.delete("/logout", ctx -> logout(ctx));
	 * javalin.get("/game", ctx -> listGame(ctx));
	 * javalin.post("/game", ctx -> createGame(ctx));
	 * javalin.put("/game", ctx -> joinGame(ctx));
	 */

	run(8080);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clearDB(io.javalin.http.Context ctx) {
	    System.out.println(ctx);
	    ctx.status(301);
    }
}
