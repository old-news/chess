package service;

import java.util.UUID;

import dataaccess.*;

public class UserService {
	private final DataAccess da;

	public UserService(DataAccess access) {
		da = access;
	}

	public RegisterResult registerUser(RegisterRequest req) throws DataAccessException {
		String username = req.username();
		String password = req.password();
		String email = req.email();
		UserData user = da.findUser(username);
		if (null != user) {
			throw new DataAccessException("username already taken");
		}
		user = new UserData(username, password, email);
		AuthData auth = generateAuth(username);
		da.addUser(user);
		da.addAuth(auth);
		return new RegisterResult(auth);
	}

	public LoginResult login(LoginRequest req) throws DataAccessException {
		String username = req.username();
		String password = req.password();
		UserData user = da.findUser(username);
		if (null == user) {
			throw new DataAccessException("wrong username");
		}
		boolean credentialsAreValid = validateCredentials(username, password, user);
		if (!credentialsAreValid) {
			throw new DataAccessException("invalid credentials");
		}
		AuthData auth = generateAuth(username);
		da.addAuth(auth);
		return new LoginResult(auth);
		
	}

	public LogoutResult logout(LogoutRequest req) throws DataAccessException {
		String authToken = req.authToken();
		AuthData auth = da.findAuth(authToken);
		if (null == auth) {
			throw new DataAccessException("no active session");
		}
		da.deleteAuth(authToken);
		return new LogoutResult(true);
	}

	private static AuthData generateAuth(String username) {
		String token = generateAuthToken();
		return new AuthData(username, token);
	}

	private static String generateAuthToken() {
		return UUID.randomUUID().toString();
	}

	private static boolean validateCredentials(String username, String password, UserData user) {
		return username.equals(user.username()) && password.equals(user.password());
	}
}
