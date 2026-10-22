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
		String authToken = req.authToken();
		AuthData auth = da.findAuth(authToken);
		if (null == auth) {
			throw new DataAccessException("not logged in");
		}
		Collection<GameData> games = da.getGames();
		return new ListGamesResult(games);
	}

	public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
		AuthData auth = da.findAuth(req.authToken());
		if (null == auth) {
			throw new DataAccessException("not logged in");
		}
		String username = auth.username();
		String gameName = req.gameName();
		int gameID = generateGameID();
		ChessGame gameObject = new ChessGame();
		GameData gameData = new GameData(gameID, null, null, gameName, gameObject);
		da.addGame(gameData);
		return new CreateGameResult(gameData, gameID);
	}

	public JoinGameResult joinGame(JoinGameRequest req) throws DataAccessException {
		AuthData auth = da.findAuth(req.authToken());
		if (null == auth) {
			throw new DataAccessException("not logged in");
		}
		GameData game = da.findGame(req.gameID());
		if (null == game) {
			throw new DataAccessException("game not found");
		}
		boolean isColorTaken = checkColorTaken(req.playerColor(), game);
		if (isColorTaken) {
			throw new DataAccessException("color already taken");
		}
		GameData joinedGame = addUserToGame(auth.username(), req.playerColor(), game);
		da.overwriteGame(joinedGame);
		return new JoinGameResult(joinedGame);
	}

	private static boolean checkColorTaken(String playerColor, GameData game) {
		if (null == playerColor) {
			return false;
		}
		playerColor = playerColor.toLowerCase();
		if (playerColor.equals("white")) {
			return null != game.whiteUsername();
		} else if (playerColor.equals("black")) {
			return null != game.blackUsername();
		}
		return false;
	}

	private static GameData addUserToGame(String username, String playerColor, GameData game) throws DataAccessException {
		/*
		 * if (username == game.whiteUsername() || username == game.blackUsername()) {
			 * game = new GameData(game.gameID(), null, null, game.gameName(), game.game());
		 * }
		 */
		if (null == playerColor) {
			throw new DataAccessException("invalid join color");
		}
		playerColor = playerColor.toLowerCase();
		if (playerColor.equals("white")) {
			return new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
		} else if (playerColor.equals("black")) {
			return new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
		}
		throw new DataAccessException("invalid join color");
	}

	private int generateGameID() {
		int id = 0;
		do {
			id += (new Random()).nextInt((int) Math.pow(2, 31) - 1);
		} while (null != da.findGame(id));
		return id;
	}
}
