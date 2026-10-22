package dataaccess;

import server.*;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

public class RamAccess implements DataAccess {
	private Map<String, UserData> users;
	private Map<String, AuthData> auths;
	private Map<Integer, GameData> games;

	public RamAccess() {
		users = new HashMap<>();
		auths = new HashMap<>();
		games = new HashMap<>();
	}

	public UserData findUser(String username) {
		return users.getOrDefault(username, null);
	}

	public AuthData findAuth(String authToken) {
		return auths.getOrDefault(authToken, null);
	}

	public GameData findGame(int gameID) {
		return games.getOrDefault(gameID, null);
	}

	public Collection<GameData> getGames() {
		Collection<GameData> ogames = new HashSet<>();
		games.forEach((key, value) -> {
			ogames.add(value);
		});
		return ogames;
	}

	public void addUser(UserData user) {
		users.put(user.username(), user);
	}

	public void addGame(GameData game) {
		games.put(game.gameID(), game);
	}

	public void addAuth(AuthData auth) {
		auths.put(auth.authToken(), auth);
	}

	public void overwriteGame(GameData game) {
		games.put(game.gameID(), game);
	}

	public void deleteAuth(String authToken) {
		auths.remove(authToken);
	}

	public void clearUsers() {
		users = new HashMap<>();
	}

	public void clearGames() {
		games = new HashMap<>();
	}

	public void clearAuth() {
		auths = new HashMap<>();
	}
}
