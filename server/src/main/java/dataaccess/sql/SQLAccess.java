package dataaccess.sql;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
import server.*;
import service.*;
import chess.ChessGame;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

public class SQLAccess implements DataAccess {

	public SQLAccess () {
		setupDatabase();
	}

	private void setupDatabase () {
		try {
			DatabaseManager.createDatabase();
		} catch (DataAccessException e) {
			System.out.println("ERR: database creation failed");
		}
		runStatement(
				"""
				CREATE TABLE IF NOT EXISTS users (
					Username text,
					PasswordHash text,
					Email text
				);
				"""
		);
		runStatement(
				"""
				CREATE TABLE IF NOT EXISTS auths (
					Username text,
					AuthToken text
				);
				"""
		);
		runStatement(
				"""
				CREATE TABLE IF NOT EXISTS games (
					GameID int,
					WhiteUsername text,
					BlackUsername text,
					GameName text,
					GameObject text
				)
				"""
		);
	}

	private boolean runStatements(List<String> statements) {
		try (var conn = DatabaseManager.getConnection()) {
			for (String statement : statements) {
				try (var preparedStatement = conn.prepareStatement(statement)) {
					if (!preparedStatement.execute()) {
						return false;
					}
					// response.next();
					// return response;
				}
			}
		} catch (SQLException | DataAccessException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	private boolean runStatement(String statement) {
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				return preparedStatement.execute();
			}
		} catch (SQLException | DataAccessException e) {
			return false;
		}
	}

	private java.sql.ResultSet getQuery(String statement) {
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				java.sql.ResultSet query = preparedStatement.executeQuery();
				query.next();
				return query;
			}
		} catch (SQLException | DataAccessException e) {
			return null;
		}
	}

	private List<Object> runQuery(String statement, List<String> keys) {
		java.sql.ResultSet response = null;
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				response = preparedStatement.executeQuery();
				try {
					response.next();
				} catch (java.sql.SQLException e) {
					return new ArrayList<>();
				}
				List<Object> values = new ArrayList<>();
				int index = 1;
				for (String key : keys) {
					try {
						values.add(response.getInt(index));
						index++;
						continue;
					} catch (java.sql.SQLException e) {}
					
					try {
						values.add(response.getString(key));
					} catch (java.sql.SQLException e) {}

				}

				return values;
			}
		} catch (SQLException | DataAccessException e) {
			return null;
		}

	}

	public UserData findUser(String username) {
		List<Object> data = runQuery(String.format("SELECT * FROM users WHERE Username = '%s'", username), List.of("Username", "PasswordHash", "Email"));
		if (null == data || data.isEmpty()) {
			return null;
		}
		return new UserData((String) data.get(0), (String) data.get(1), (String) data.get(2));
	}

	public AuthData findAuth(String authToken) {
		List<Object> data = runQuery(String.format("SELECT * FROM auths WHERE AuthToken = '%s'", authToken), List.of("Username", "AuthToken"));
		if (null == data || data.isEmpty()) {
			return null;
		}
		return new AuthData((String) data.get(0), (String) data.get(1));
	}

	public GameData findGame(int gameID) {
		List<Object> data = runQuery(String.format("SELECT * FROM games WHERE GameID = %d", gameID), List.of("GameID", "WhiteUsername", "BlackUsername", "GameName", "GameObject"));
		if (null == data || data.isEmpty()) {
			return null;
		}
		ChessGame gameObject = (new Gson().newBuilder().enableComplexMapKeySerialization().create()).fromJson((String) data.get(4), ChessGame.class);
		return new GameData((Integer) data.get(0), (String) data.get(1), (String) data.get(2), (String) data.get(3), gameObject);
	}

	public Collection<GameData> getGames() {
		String statement = "SELECT * FROM games";
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				java.sql.ResultSet response = preparedStatement.executeQuery();
				Collection<GameData> games = new ArrayList<>();
				try {
					while (response.next()) {
						ChessGame gameObject = (new Gson()).fromJson(response.getString("GameObject"), ChessGame.class);
						GameData data = new GameData(
								response.getInt(1),
								response.getString("WhiteUsername"),
								response.getString("BlackUsername"),
								response.getString("GameName"),
								gameObject
						);
						games.add(data);
					}
				} catch (java.sql.SQLException e) {
					return games;
				}
				return games;
			}
		} catch (SQLException | DataAccessException e) {
			return null;
		}


	}

	public void addUser(UserData user) {
		String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
		String statement = String.format("INSERT INTO users VALUES ('%s', '%s', '%s')", user.username(), hashedPassword, user.email());
		runStatement(statement);
	}

	public void addAuth(AuthData auth) {
		runStatement(String.format("INSERT INTO auths VALUES ('%s', '%s')", auth.username(), auth.authToken()));
	}

	public void addGame(GameData game) {
		runStatement(String.format(
				"INSERT INTO games VALUES (%d, '%s', '%s', '%s', '%s')",
				game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), (new Gson().newBuilder().enableComplexMapKeySerialization().create()).toJson(game.game())
		));
	}

	public void overwriteGame(GameData game) {
		runStatement(String.format(
				"UPDATE games SET GameID = %d, WhiteUsername = '%s', BlackUsername = '%s', GameName = '%s', GameObject = '%s' WHERE GameID = %d",
				game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), (new Gson().newBuilder().enableComplexMapKeySerialization().create()).toJson(game.game()), game.gameID()
		));
	}

	public void deleteAuth(String authToken) {
		runStatement(String.format("DELETE FROM auths WHERE AuthToken = '%s'", authToken));
	}

	public void clearUsers() {
		runStatement("DELETE FROM users");
	}

	public void clearAuth() {
		runStatement("DELETE FROM auths");
	}

	public void clearGames() {
		runStatement("DELETE FROM games");
	}

}
