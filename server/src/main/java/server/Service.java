package server;

import java.util.UUID;

import java.util.Map;
import java.util.HashMap;

import dataacess.DataAccessException;


public class Service {
	private final DataAccess da;

	public Map<String, String> register(String username, String password, String email) {
		var user = da.getUser(username);
		if (null != user)
			return DataAccessException("Error: username already taken");
		user = new UserData(username, password, email);
		AuthData auth = createAuthData(username);
		da.add(user);
		da.add(auth);
		return Map.of("username", user, "authToken", auth.token);
	}

	private AuthData createAuthData(String username) {
		String token = generateToken();
		return new AuthData(username, token);
	}

	private static String generateToken() {
		return UUID.randomUUID().toString();
	}
}
