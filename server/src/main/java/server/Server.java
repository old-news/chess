package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;
    private final Handler handler;

    public Server() {
	    handler = new Handler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

	javalin.post("/user", ctx -> registerUser(ctx));
	javalin.post("/session", ctx -> login(ctx));
	javalin.delete("/session", ctx -> logout(ctx));
	javalin.get("/game", ctx -> listGames(ctx));
	javalin.post("/game", ctx -> createGame(ctx));
	javalin.put("/game", ctx -> joinGame(ctx));
	javalin.delete("/db", ctx -> clearDB(ctx));

	// run(8080);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void updateCTX(io.javalin.http.Context ctx, HandlerResult res) {
	    ctx.status(res.status());
	    ctx.result(res.body());
    }

    private void registerUser(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.registerUser(ctx.body());
	    updateCTX(ctx, res);
    }

    private void login(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.login(ctx.body());
	    updateCTX(ctx, res);
    }

    private void logout(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.logout(ctx.body());
	    updateCTX(ctx, res);
    }

    private void listGames(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.listGames(ctx.body());
	    updateCTX(ctx, res);
    }

    private void createGame(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.createGame(ctx.body());
	    updateCTX(ctx, res);
    }

    private void joinGame(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.joinGame(ctx.body());
	    updateCTX(ctx, res);
    }

    private void clearDB(io.javalin.http.Context ctx) {
	    HandlerResult res = handler.clearDB(ctx.body());
	    updateCTX(ctx, res);
    }
}
