package server;

import dataaccess.*;
import com.google.gson.Gson;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.List;

public class Handler {
	private final DataAccess da;
	private final UserService us;
	private final GameService gs;
	private final ClearService cs;
	private final Gson serializer;

	public Handler() {
		da = new RamAccess();
		us = new UserService(da);
		gs = new GameService(da);
		cs = new ClearService(da);
		serializer = new Gson();
	}

	private HandlerResult handlerError(int status, String message) {
		Map<String, Object> body = Map.of("message", "Error: " + message);
		return new HandlerResult(status, body);
	}

	public HandlerResult registerUser(String requestBody) {
		int status = 200;
		Map<String, Object> body;
		var request = serializer.fromJson(requestBody, HashMap.class);
		if (null == request.get("username") || null == request.get("password") || null == request.get("email")) {
			return handlerError(400, "Malformed request");
		}
		RegisterRequest req = new RegisterRequest(
			(String) request.get("username"),
			(String) request.get("password"),
			(String) request.get("email")
		);
		try {
			RegisterResult res = us.registerUser(req);
			body = Map.of("username", res.auth().username(), "authToken", res.auth().authToken());
		} catch (DataAccessException e) {
			body = Map.of("message", "Error: " + e.getMessage());
			status = 403;
		}
		return new HandlerResult(status, body);
	}

	public HandlerResult login(String requestBody) {
		int status = 200;
		Map<String, Object> body;
		var request = serializer.fromJson(requestBody, HashMap.class);
		if (null == request.get("username") || null == request.get("password")) {
			return handlerError(400, "Malformed request");
		}
		LoginRequest req = new LoginRequest((String) request.get("username"), (String) request.get("password"));
		try {
			LoginResult res = us.login(req);
			body = Map.of("username", res.auth().username(), "authToken", res.auth().authToken());
		} catch (DataAccessException e) {
			body = Map.of("message", "Error: " + e.getMessage());
			if (e.getMessage().equals("wrong username")) {
				status = 401;
			} else if (e.getMessage().equals("invalid credentials")) {
				status = 401;
			}
		}
		return new HandlerResult(status, body);
	}

	public HandlerResult logout(String authToken) {
		int status = 200;
		Map<String, Object> body;
		if (null == authToken) {
			return handlerError(400, "Malformed request");
		}
		LogoutRequest req = new LogoutRequest(authToken);
		try {
			LogoutResult res = us.logout(req);
			body = Map.of("success", java.lang.Boolean.toString(res.succeeded()));
		} catch (DataAccessException e) {
			body = Map.of("message", "Error: " + e.getMessage());
			status = 401;
		}
		return new HandlerResult(status, body);
	}

	public HandlerResult listGames(String authToken) {
		int status = 200;
		Map<String, Object> body;
		if (null == authToken) {
			return handlerError(400, "Malformed request");
		}
		ListGamesRequest req = new ListGamesRequest(authToken);
		try {
			ListGamesResult res = gs.listGames(req);
			body = Map.of("games", res.games());
		} catch (DataAccessException e) {
			body = Map.of("message", "Error: " + e.getMessage());
			status = 401;
		}
		return new HandlerResult(status, body);
	}

	public HandlerResult createGame(String authToken, String requestBody) {
		int status = 200;
		Map<String, Object> body;
		var request = serializer.fromJson(requestBody, HashMap.class);
		if (null == authToken || null == request.get("gameName")) {
			return handlerError(400, "Malformed request");
		}
		CreateGameRequest req = new CreateGameRequest(authToken, (String) request.get("gameName"));
		try {
			CreateGameResult res = gs.createGame(req);
			body = Map.of("game", res.game(), "gameID", res.gameID());
		} catch (DataAccessException e) {
			body = Map.of("message", "Error: " + e.getMessage());
			status = 401;
		}
		return new HandlerResult(status, body);
	}

	public HandlerResult joinGame(String authToken, String requestBody) {
		int status = 200;
		Map<String, Object> body;
		var request = serializer.fromJson(requestBody, HashMap.class);
		if (null == authToken || null == request.get("gameID")) {
			return handlerError(400, "Malformed request");
		}
		JoinGameRequest req = new JoinGameRequest(
			authToken,
			(String) request.get("playerColor"),
			((Number) request.get("gameID")).intValue()
		);
		try {
			JoinGameResult res = gs.joinGame(req);
			body = Map.of("game", res.game());
		} catch (DataAccessException e) {
			body = Map.of("message", "Error: " + e.getMessage());
			switch (e.getMessage()) {
				case "not logged in" -> status = 401;
				case "game not found" -> status = 404;
				case "color already taken" -> status = 403;
				case "invalid join color" -> status = 400;
			}
		}
		return new HandlerResult(status, body);
	}

	public HandlerResult clearDB() {
		int status = 200;
		Map<String, Object> body;
//		try {
			ClearDBResult res = cs.clearDB();
			body = Map.of("succeeded", java.lang.Boolean.toString(res.success()));
//		} catch (DataAccessException e) {
//			bodyItem = Map.of("message", "Error: " + e.getMessage());
//			status = 400;
//		}
		return new HandlerResult(status, body);
	}

	/*
	 * interface ServiceCall<Req, Res> {
		 * Res apply(Req req) throws DataAccessException;
	 * }

	 * private <Req, Res> HandlerResult handle (
		 * String reqBody,
		 * Function<Map<String, String>, Req> inputFormatter,
		 * Function<Res, Map> outputFormatter,
		 * ServiceCall<Req, Res> permutation,
		 * Map<String, Integer> errorMap
	 * ) {
		 * int status = 200;
		 * Map<String, Object> bodyItem;
		 * HashMap request = serializer.fromJson(reqBody, HashMap.class);
		 * Req req = inputFormatter.apply(request);
		 * try {
			 * Res res = permutation.apply(req);
			 * bodyItem = outputFormatter.apply(res);
		 * } catch (DataAccessException e) {
			 * bodyItem = Map.of("message", "Error: " + e.getMessage());
			 * status = errorMap.get(e.getMessage());
		 * }
		 * String body = serializer.toJson(bodyItem);
		 * return new HandlerResult(status, body);
	 * }

	 * public HandlerResult createGame(String requestBody) {
		 * return handle(
			 * requestBody,
			 * request -> new CreateGameRequest(Map.of("authToken", request.get("authToken"), "gameName", request.get("gameName"))),
			 * res -> Map.of("game", res.game()),
			 * gs::createGame,
			 * Map.of("not logged in", 401)
		 * );
	 * }
	 */
}
