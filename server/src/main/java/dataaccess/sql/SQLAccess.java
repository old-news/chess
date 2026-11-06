package dataaccess.sql;

import dataaccess.*;
import server.*;
import service.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

class SQLAccess {

	public SQLAccess () throws DataAccessException {
		setupDatabase();
	}

	private void setupDatabase () throws DataAccessException {
		DatabaseManager.createDatabase();
		runStatements(List.of(
				"""
				CREATE TABLE IF NOT EXISTS users (
					Username text,
					PasswordHash text,
					Email text
				);
				""",
				"""
				CREATE TABLE IF NOT EXISTS auths (
					Username text,
					AuthToken text
				);
				""",
				"""
				CREATE TABLE IF NOT EXISTS games (
					GamdID int,
					WhiteUsername text,
					BlackUsername text,
					GameName text,
					GameObject text
				)
				"""
		));
	}

	private void runStatements(List<String> statements) {
		try (var conn = DatabaseManager.getConnection()) {
			for (String statement : statements) {
				try (var preparedStatement = conn.prepareStatement(statement)) {
					var response = preparedStatement.executeQuery();
					// response.next();
					// return response;
				} catch (java.sql.SQLException e) {return;}
			}
		} catch (SQLException | DataAccessException e) {
			return;
		}
	}

	private java.sql.ResultSet runStatement(String statement) {
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				var response = preparedStatement.executeQuery();
				response.next();
				return response;
			} catch (java.sql.SQLException e) {return null;}
		} catch (SQLException | DataAccessException e) {
			return null;
		}
	}

	private List<Object> runQuery(String statement, List<String> keys) {
		var response = runStatement(statement);
		try {
			response.next();
		} catch (java.sql.SQLException e) {
			return null;
		}
		List<Object> values = new ArrayList<Object>();
		for (String key : keys) {
			try {
				values.add(response.getString(key));
			} catch (java.sql.SQLException e) {}
			
			try {
				values.add(response.getInt(1));
			} catch (java.sql.SQLException e) {}
		}

		return values;
	}

	public UserData findUser(String username) {
		List<Object> data = runQuery(String.format("SELECT * FROM users WHERE Username = '%s'", username), List.of("Username", "Password", "Email"));
		return new UserData((String) data.get(0), (String) data.get(1), (String) data.get(2));
	}

	public AuthData findAuth(String authToken) {
		List<Object> data = runQuery(String.format("SELECT * FROM auths WHERE AuthToken = '%s'", authToken), List.of("Username", "AuthToken"));
		return new AuthData((String) data.get(0), (String) data.get(1));
	}

	public GameData findGame(int gameID) {
		List<Object> data = runQuery(String.format("SELECT * FROM games WHERE GameID = %d", gameID), List.of("GameID", "WhiteUsername", "BlackUsername", "GameName", "GameObject"));
		return new GameData((int) data.get(0), (String) data.get(1), (String) data.get(2), (String) data.get(3), (String) data.get(4));
	}

	public Collection<GameData> getGames() {
		var response = runStatement("SELECT * FROM games");
		Collection<GameData> games = new ArrayList<>();
		while (response.next()) {
			GameData data = new GameData(
					response.getInt(1),
					response.getString("WhiteUsername"),
					response.getString("BlackUsername"),
					response.getString("GameName"),
					response.getString("GameObject"),
			);
			games.add(data);
		}
		return games;
	}

	public void addUser(UserData user) {
		runStatement(String.format("INSERT INTO users VALUES (%s, %s, %s)", user.username(), user.password(), user.email()));
	}

	public void addAuth(AuthData auth) {
		runStatement(String.format("INSERT INTO auths VALUES (%s, %s)", auth.username(), auth.authToken()));
	}

	public void addGame(GameData game) {
		runStatement(String.format(
				"INSERT INTO games VALUES (%d, %s, %s, %s, %s)",
				game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()
		));
	}

	public void overwriteGame(GameData game) {
		runStatement(String.format(
				"UPDATE games SET GameID = %d, WhiteUsername = %s, BlackUsername = %s, GameName = %s, GameObject = %s WHERE GameID = %d",
				game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), game.gameID()
		));
	}

	public void deleteAuth(String authToken) {
		runStatement(String.format("DELETE FROM auths WHERE AuthToken = %s", authToken));
	}

	public void clearUsers() {
		runStatement("DELETE FROM users");
	}

	public void clearAuths() {
		runStatement("DELETE FROM auths");
	}

	public void clearGames() {
		runStatement("DELETE FROM games");
	}
}
