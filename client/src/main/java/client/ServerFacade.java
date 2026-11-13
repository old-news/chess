package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.InternalServerErrorResponse;
import service.*;
import dataaccess.*;
import dataaccess.sql.*;
import server.*;
import chess.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class ServerFacade {
	private List<GameData> games;
	private int port;
	private String url;
	private static HttpClient httpClient;
	private Gson serializer;
	private UserService us;
	private GameService gs;
	private DataAccess da;

	public ServerFacade(int p) {
		games = new ArrayList();
		port = p;
		url = String.format("http://localhost:%d/", p);
		httpClient = HttpClient.newHttpClient();
		serializer = new Gson().newBuilder().serializeNulls().enableComplexMapKeySerialization().create();
		// serializer = new Gson();
		da = new SQLAccess();
		us = new UserService(da);
		gs = new GameService(da);
	}

	public void stop() {
		return;
	}

	private String sendReqJson(HttpRequest req) {
		try {
			HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
			if (res.statusCode() >= 300 || res.statusCode() < 200) {
				String message = (String) ((Map) serializer.fromJson(res.body(), Map.class)).get("message");
				throw new RuntimeException(message + " (" + Integer.toString(res.statusCode()) + ")");
			}
			return res.body();
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> sendReq(HttpRequest req) {
		return serializer.fromJson(sendReqJson(req), HashMap.class);
	}

	public AuthData registerUser(List<String> data) {
		try {
			Map<String, String> req = Map.of("username", data.get(0), "password", data.get(1), "email", data.get(2));
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "user"))
				.timeout(java.time.Duration.ofMillis(5000))
				.POST(HttpRequest.BodyPublishers.ofString(serializer.toJson(req)))
				.build();
			Map<String, Object> res = sendReq(hreq);
			return new AuthData((String) res.get("username"), (String) res.get("authToken"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }

	public AuthData login(List<String> data) {
		try {
			Map<String, String> req = Map.of("username", data.get(0), "password", data.get(1));
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "session"))
				.timeout(java.time.Duration.ofMillis(5000))
				.POST(HttpRequest.BodyPublishers.ofString(serializer.toJson(req)))
				.build();
			Map<String, Object> res = sendReq(hreq);
			return new AuthData((String) res.get("username"), (String) res.get("authToken"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public GameData createGame(List<String> data) {
		try {
			Map<String, String> req = Map.of("gameName", data.get(1));
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "game"))
				.timeout(java.time.Duration.ofMillis(5000))
				.POST(HttpRequest.BodyPublishers.ofString(serializer.toJson(req)))
				.header("authorization", data.get(0))
				.build();
			String json = sendReqJson(hreq);
			return gameFromJson(json);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private GameData gameFromJson(String json) {
		GameData res = serializer.fromJson(json, GameData.class);
		Map<String, Object> tree = serializer.fromJson(json, HashMap.class);
		Map<String, Object> minitree = (Map) tree.get("game");
		int gameID = ((Double) minitree.get("gameID")).intValue();
		String gameName = (String) minitree.get("gameName");
		String whiteName = (String) minitree.get("whiteUsername");
		String blackName = (String) minitree.get("blackUsername");
		res = new GameData(gameID, whiteName, blackName, gameName, res.game());
		return res;
	}

	public List<GameData> listGames(String authToken) {
		try {
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "game"))
				.timeout(java.time.Duration.ofMillis(5000))
				.GET()
				.header("authorization", authToken)
				.build();
			String json = sendReqJson(hreq);
			System.out.println(json);
			Type t = new TypeToken<List<GameData>>(){}.getType();
			System.out.println((List<GameData>) serializer.fromJson(json, t));
			return List.of();
			// return serializer.fromJson(json, GameData.class);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public GameData joinGame(List<String> data) {
		try {
			Integer gameID = getGameIDByUserIndex(Integer.parseInt(data.get(2)));
			if (null == gameID) {
				return null;
			}
			Map<String, Object> req = Map.of("playerColor", data.get(1), "gameID", gameID);
			System.out.println(data);
			System.out.println(req);
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "game"))
				.timeout(java.time.Duration.ofMillis(5000))
				.PUT(HttpRequest.BodyPublishers.ofString(serializer.toJson(req)))
				.header("authorization", data.get(0))
				.build();
			String json = sendReqJson(hreq);
			return gameFromJson(json);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Integer getGameIDByUserIndex(int index) {
			try {
				Integer gameID = games.get(index - 1).gameID();
				return gameID;
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
	}

	public GameData getGame(int gameID) {
		return da.findGame(gameID);
	}

	public GameData loadGame(List<String> data) {
		if (da.findAuth(data.get(0)) == null) {
			return null;
		}
		Integer gid = getGameIDByUserIndex(Integer.parseInt(data.get(1)));
		if (null == gid) {
			return null;
		}
		return getGame(gid);
	}

	public boolean logout(String authToken) {
		try {
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "session"))
				.timeout(java.time.Duration.ofMillis(5000))
				.DELETE()
				.header("authorization", authToken)
				.build();
			Map<String, Object> res = sendReq(hreq);
			return "true".equals(res.get("success"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public void clear() {
		try {
			// Map<String, Object> req = Map.of("playerColor", data.get(1), "gameID", gameID);
			HttpRequest hreq = HttpRequest.newBuilder()
				.uri(new URI(url + "db"))
				.timeout(java.time.Duration.ofMillis(5000))
				.DELETE()
				.build();
			Map<String, Object> res = sendReq(hreq);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
