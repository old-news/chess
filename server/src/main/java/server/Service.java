package server;

import java.util.UUID;

import java.util.Map;
import java.util.HashMap;

import dataaccess.*;


public class Service {
	private final DataAccess da;

	public Service() {
		da = new DataAccess();
	}

	public Map<String, String> registerUser(String username, String password, String email) throws DataAccessException {
		var user = da.getUser(username);
		if (null != user)
			throw new DataAccessException("Error: username already taken");
		user = new UserData(username, password, email);
		AuthData auth = createAuthData(username);
		da.addUser(user);
		da.addAuth(auth);
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
