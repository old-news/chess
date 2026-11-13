package client;

import server.*;
import service.*;
import dataaccess.*;
import dataaccess.sql.*;

import java.util.List;
import java.util.ArrayList;

public class ServerFacade {
	private DataAccess da;
	private UserService us;
	private GameService gs;
	private List<GameData> games;

	public ServerFacade() {
		da = new SQLAccess();
		us = new UserService(da);
		gs = new GameService(da);
		games = new ArrayList();
	}

	public int run(int n) {
		return n;
	}

	public void stop() {
		return;
	}

	public AuthData registerUser(List<String> data) {
		try {
			RegisterResult res = us.registerUser(new RegisterRequest(data.get(0), data.get(1), data.get(2)));
			return res.auth();
		} catch (DataAccessException e) {
			return null;
		}
	}

	public AuthData login(List<String> data) {
		try {
			LoginResult res = us.login(new LoginRequest(data.get(0), data.get(1)));
			return res.auth();
		} catch (DataAccessException e) {
			return null;
		}
	}

	public GameData createGame(List<String> data) {
		try {
			CreateGameResult res = gs.createGame(new CreateGameRequest(data.get(0), data.get(1)));
			games.add(res.game());
			return res.game();
		} catch (DataAccessException e) {
			return null;
		}
	}

	public List<GameData> listGames(String authToken) {
		try {
			ListGamesResult res = gs.listGames(new ListGamesRequest(authToken));
			games = new ArrayList(res.games()); 
			return games;
		} catch (DataAccessException e) {
			return null;
		}
	}

	public GameData joinGame(List<String> data) {
		try {
			if (null == games) {
				listGames(data.get(0));
			}
			Integer gameID = getGameIDByUserIndex(Integer.parseInt(data.get(2)));
			if (null == gameID) {
				return null;
			}
			JoinGameRequest req = new JoinGameRequest(data.get(0), data.get(1), gameID);
			JoinGameResult res = gs.joinGame(req);
			return res.game();
		} catch (DataAccessException e) {
			return null;
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
			LogoutResult res = us.logout(new LogoutRequest(authToken));
			return res.succeeded();
		} catch (DataAccessException e) {
			return false;
		}
	}

	public void clear() {
		da.clearUsers();
		da.clearAuth();
		da.clearGames();
	}
}
