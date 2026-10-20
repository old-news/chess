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
		da = new DataAccess();
		us = new UserService(da);
		gs = new GameService(da);
		cs = new ClearService(da);
		serializer = new Gson();
	}

	public HandlerResult registerUser(String requestBody) {
		int status = 200;
		Map<String, String> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		RegisterRequest req = new RegisterRequest((String) request.get("username"), (String) request.get("password"), (String) request.get("email"));
		try {
			RegisterResult res = us.registerUser(req);
			bodyItem = Map.of("username", res.auth().username(), "authToken", res.auth().authToken());
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			status = 403;
		}
		String body = serializer.toJson(bodyItem);
		return new HandlerResult(status, body);
	}

	public HandlerResult login(String requestBody) {
		int status = 200;
		Map<String, String> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		LoginRequest req = new LoginRequest((String) request.get("username"), (String) request.get("password"));
		try {
			LoginResult res = us.login(req);
			bodyItem = Map.of("username", res.auth().username(), "authToken", res.auth().authToken());
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			if (e.getMessage().equals("wrong username")) {
				status = 4001;
			} else if (e.getMessage().equals("invalid credentials")) {
				status = 401;
			}
		}
		String body = serializer.toJson(bodyItem);
		return new HandlerResult(status, body);
	}

	public HandlerResult logout(String requestBody) {
		int status = 200;
		Map<String, String> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		LogoutRequest req = new LogoutRequest((String) request.get("authToken"));
		try {
			LogoutResult res = us.logout(req);
			bodyItem = Map.of("success", java.lang.Boolean.toString(res.succeeded()));
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			status = 404;
		}
		String body = serializer.toJson(bodyItem);
		return new HandlerResult(status, body);
	}

	public HandlerResult listGames(String requestBody) {
		int status = 200;
		Map<String, Object> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		ListGamesRequest req = new ListGamesRequest((String) request.get("authToken"));
		try {
			ListGamesResult res = gs.listGames(req);
			bodyItem = Map.of("games", res.games());
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			status = 401;
		}
		String body = serializer.toJson(bodyItem);
		return new HandlerResult(status, body);
	}

	public HandlerResult createGame(String requestBody) {
		int status = 200;
		Map<String, String> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		CreateGameRequest req = new CreateGameRequest((String) request.get("authToken"), (String) request.get("gameName"));
		try {
			CreateGameResult res = gs.createGame(req);
			bodyItem = Map.of("game", res.game().toString());
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			status = 401;
		}
		String body = serializer.toJson(bodyItem);
		return new HandlerResult(status, body);
	}

	public HandlerResult joinGame(String requestBody) {
		int status = 200;
		Map<String, String> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		JoinGameRequest req = new JoinGameRequest((String) request.get("authToken"), (String) request.get("playerColor"), (int) request.get("gameID"));
		try {
			JoinGameResult res = gs.joinGame(req);
			bodyItem = Map.of("game", res.game().toString());
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			switch (e.getMessage()) {
				case "not logged in" -> status = 401;
				case "game not found" -> status = 404;
				case "color already taken" -> status = 403;
			}
		}
		String body = serializer.toJson(bodyItem);
		return new HandlerResult(status, body);
	}

	public HandlerResult clearDB(String requestBody) {
		int status = 200;
		Map<String, String> bodyItem;
		var request = serializer.fromJson(requestBody, HashMap.class);
		try {
			ClearDBResult res = cs.clearDB();
			bodyItem = Map.of("succeeded", java.lang.Boolean.toString(res.success()));
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			status = 400;
		}
		String body = serializer.toJson(bodyItem);
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
