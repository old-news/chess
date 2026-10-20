package server;

import java.util.Random;

import dataaccess.*;
import shared.ChessGame;

public class GameService {
	private final DataAccess da;

	public GameService(DataAccess access) {
		da = access;
	}

	public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
		AuthData auth = da.getAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("not logged in");
		Collection<GameData> games = da.getGames();
		return new ListGamesResult(games);
	}

	public CreateGameResult createGame(CreateGameRequest req) {
		AuthData auth = da.getAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("not logged in");
		int gameID = generateGameID();
		ChessGame gameObject = new ChessGame();
		GameData gameData = new GameData(gameID, auth.username, null, req.gameName, gameObject);
		return new CreateGameResult(gameData);
	}

	public JoinGameResult joinGame(JoinGameRequest req) {
		AuthData auth = da.getAuth(req.authToken);
		if (null == auth)
			throw new DataAccessException("not logged in");
		GameData game = da.getGame(req.gameID);
		if (null == game)
			throw new DataAccessException("game not found");
		boolean isColorTaken = checkColorNotTaken(req.playerColor, game);
		if (true == isColorTaken)
			throw new DataAccessException("color already taken");
		addUserToGame(auth.username, req.playerColor, game);
		da.overwriteGame(game);
		return new JoinGameResult(game);
	}

	private static void addUserToGame(String username, String playerColor, GameData game) throws DataAccessException {
		if ("white" == playerColor) {
			game.whiteUsername = username
		} else if ("black" == playerColor) {
			game.blackUsername = username
		} else {
			throw new DataAccessException("invalid join color");
		}
	}

	private static int generateGameID() {
		int id;
		do {
			id += (new Random()).nextInt(Math.pow(2, 31));
		} while (null != da.findGame(gameID));
		return id;
	}
}
