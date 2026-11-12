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
		games = null;
	}

	public void run(int n) {
		return;
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
			int gameID = 0;
			while (0 == gameID) {
				try {
					gameID = games.get(Integer.parseInt(data.get(2)) - 1).gameID();
				} catch (IndexOutOfBoundsException e) {
					return null;
				}
			}
			JoinGameRequest req = new JoinGameRequest(data.get(0), data.get(1), gameID);
			System.out.println(req);
			JoinGameResult res = gs.joinGame(req);
			return res.game();
		} catch (DataAccessException e) {
			return null;
		}
	}

	public GameData getGame(int gameID) {
		return da.findGame(gameID);
	}

	public GameData loadGame(List<String> data) {
		return null;
	}

	public boolean logout(String authToken) {
		try {
			LogoutResult res = us.logout(new LogoutRequest(authToken));
			return res.succeeded();
		} catch (DataAccessException e) {
			return false;
		}
	}
}
