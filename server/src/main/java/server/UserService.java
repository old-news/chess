package server;

import java.util.UUID;

import dataaccess.DataAccessException;

public class UserService {
	private final DataAccess da;

	public UserService(DataAccess access) {
		da = access;
	}

	public RegisterResult register(RegisterRequest req) throws DataAccessException {
		UserData user = da.getUser(req.username);
		if (null != user)
			throw new DataAccessException("username already taken");
		user = new UserData(req.username, req.password, req.email);
		AuthData auth = new generateAuth(req.username);
		da.addUser(user);
		da.addAuth(auth);
		return new RegisterResult(auth);
	}

	public LoginResult login(LoginRequest req) throws DataAccessException {
		UserData user = da.getUser(req.username);
		if (null == user)
			throw new DataAccessException("wrong username");
		boolean credentialsAreValid = validateCredentials(req.username, req.password, user);
		if (!credentialsAreValid)
			throw new DataAccessException("invalid credentials");
		AuthData auth = generateAuth(req.username);
		da.addAuth(auth);
		return new LoginResult(auth);
		
	}

	public void logout(LogoutRequest req) {
		AuthData auth = da.findAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("no active session");
		da.deleteAuth(req.authToken);
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
		if (!username.equals(user.username) || !password.equals(user.password)) reutrn false;
		return true;
	}
}
