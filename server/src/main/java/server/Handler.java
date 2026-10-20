package server;

import dataaccess.*;
import com.google.gson.Gson;

import java.util.Map;
import java.util.HashMap;

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
		var request = serializer.fromJson(requestBody, HashMap.class);
		RegisterRequest req = new RegisterRequest((String) request.get("username"), (String) request.get("password"), (String) request.get("email"));
		Map<String, String> bodyItem;
		int status = 200;
		try {
			RegisterResult res = us.registerUser(req);
			bodyItem = Map.of("username", res.auth().username(), "authToken", res.auth().authToken());
		} catch (DataAccessException e) {
			bodyItem = Map.of("message", "Error: " + e.getMessage());
			status = 403;
		} finally {
			String body = serializer.toJson(bodyItem);
			return new HandlerResult(status, body);
		}
	}
}
