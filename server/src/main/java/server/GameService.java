package server;

import java.util.Collection;
import java.util.Random;

import dataaccess.*;
import chess.ChessGame;

public class GameService {
	private final DataAccess da;

	public GameService(DataAccess access) {
		da = access;
	}

	public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
		AuthData auth = da.findAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("not logged in");
		Collection<GameData> games = da.getGames();
		return new ListGamesResult(games);
	}

	public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
		AuthData auth = da.findAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("not logged in");
		int gameID = generateGameID();
		ChessGame gameObject = new ChessGame();
		GameData gameData = new GameData(gameID, auth.username, null, req.gameName, gameObject);
		return new CreateGameResult(gameData);
	}

	public JoinGameResult joinGame(JoinGameRequest req) throws DataAccessException {
		AuthData auth = da.findAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("not logged in");
		GameData game = da.findGame(req.gameID);
		if (null == game)
			throw new DataAccessException("game not found");
		boolean isColorTaken = checkColorNotTaken(req.playerColor, game);
		if (true == isColorTaken)
			throw new DataAccessException("color already taken");
		addUserToGame(auth.username, req.playerColor, game);
		da.overwriteGame(game);
		return new JoinGameResult(game);
	}

	private static boolean checkColorNotTaken(String playerColor, GameData game) {
		if ("white" == playerColor) {
			return null == game.whiteUsername;
		} else if ("black" == playerColor) {
			return null == game.blackUsername;
		}
		return true;
	}

	private static void addUserToGame(String username, String playerColor, GameData game) throws DataAccessException {
		if ("white" == playerColor) {
			game.whiteUsername = username
		} else if ("black" == playerColor) {
			game.blackUsername = username
		}
		throw new DataAccessException("invalid join color");
	}

	private int generateGameID() {
		int id = 0;
		do {
			id += (new Random()).nextInt((int) Math.pow(2, 31));
		} while (null != da.findGame(id));
		return id;
	}
}
