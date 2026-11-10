package dataaccess.sql;

import dataaccess.*;
import io.javalin.http.InternalServerErrorResponse;
import org.mindrot.jbcrypt.BCrypt;
import server.*;
import service.*;
import chess.ChessGame;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

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
	private static Gson serializer;

	public SQLAccess () {
		setupDatabase();
		serializer = new Gson().newBuilder().enableComplexMapKeySerialization().create();
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

	private boolean runStatement(String statement) {
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				return preparedStatement.execute();
			}
		} catch (SQLException | DataAccessException e) {
			throw new InternalServerErrorResponse(e.getMessage());
			// return false;
		}
	}

	private boolean runStatement(String statement, List<Object> items) {
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				for (int i = 0; i < items.size(); i++) {
					var item = items.get(i);
					if (item.getClass() == Integer.class) {
						preparedStatement.setInt(i + 1, (int) item);
					} else {
						preparedStatement.setString(i + 1, (String) item);
					}
				}
				return preparedStatement.execute();
			}
		} catch (SQLException | DataAccessException e) {
			throw new InternalServerErrorResponse(e.getMessage());
			// return false;
		}
	}

	private List<Object> runQuery(String statement, List<String> keys) {
		java.sql.ResultSet response = null;
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				response = preparedStatement.executeQuery();
				try {
					response.next();
				} catch (Exception e) {
					return new ArrayList<>();
				}
				List<Object> values = new ArrayList<>();
				int index = 1;
				for (String key : keys) {
					try {
						Integer val = response.getInt(index);
						if (response.wasNull()) {
							val = null;
						}
						values.add(val);
						index++;
						continue;
					} catch (java.sql.SQLException | NumberFormatException e) {}
					
					try {
						values.add(response.getString(key));
					} catch (java.sql.SQLException e) {}

				}

				return values;
			}
		} catch (SQLException | DataAccessException e) {
			throw new InternalServerErrorResponse(e.getMessage());
			// return null;
		}

	}

	public UserData findUser(String username) {
		List<Object> data = runQuery(
				String.format("SELECT * FROM users WHERE Username = '%s'", username),
				List.of("Username", "PasswordHash", "Email"
		));
		if (null == data || data.isEmpty()) {
			return null;
		}
		return new UserData((String) data.get(0), (String) data.get(1), (String) data.get(2));
	}

	public AuthData findAuth(String authToken) {
		List<Object> data = runQuery(
				String.format("SELECT * FROM auths WHERE AuthToken = '%s'", authToken),
				List.of("Username", "AuthToken"
		));
		if (null == data || data.isEmpty()) {
			return null;
		}
		return new AuthData((String) data.get(0), (String) data.get(1));
	}

	public GameData findGame(int gameID) {
		List<Object> data = runQuery(
				String.format("SELECT * FROM games WHERE GameID = %d", gameID),
				List.of("GameID", "WhiteUsername", "BlackUsername", "GameName", "GameObject"
		));
		if (null == data || data.isEmpty()) {
			return null;
		}
		return synthesizeGame(data);
	}

	private GameData synthesizeGame(List<Object> data) {
		ChessGame gameObject = serializer.fromJson((String) data.get(4), ChessGame.class);
		String blackUsername = (String) data.get(2);
		/*
		 * if ("null".equals(blackUsername)) {
			 * blackUsername = null;
		 * }
		 */
		String whiteUsername = (String) data.get(1);
		/*
		 * if ("null".equals(whiteUsername)) {
			 * whiteUsername = null;
		 * }
		 */
		return new GameData((Integer) data.get(0), whiteUsername, blackUsername, (String) data.get(3), gameObject);
	}

	public Collection<GameData> getGames() {
		String statement = "SELECT * FROM games";
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement(statement)) {
				java.sql.ResultSet response = preparedStatement.executeQuery();
				Collection<GameData> games = new ArrayList<>();
				try {
					while (response.next()) {
						GameData data = synthesizeGame(Arrays.asList(
							response.getInt(1),
							response.getString("WhiteUsername"),
							response.getString("BlackUsername"),
							response.getString("GameName"),
							response.getString("GameObject")
						));
						/*
						 * ChessGame gameObject = (new Gson()).fromJson(response.getString("GameObject"), ChessGame.class);
						 * GameData data = new GameData(
								 * response.getInt(1),
								 * response.getString("WhiteUsername"),
								 * response.getString("BlackUsername"),
								 * response.getString("GameName"),
								 * gameObject
						 * );
						 */
						games.add(data);
					}
				} catch (java.sql.SQLException | NumberFormatException e) {
					return games;
				}
				return games;
			}
		} catch (SQLException | DataAccessException e) {
			throw new InternalServerErrorResponse(e.getMessage());
			// return null;
		}


	}

	public void addUser(UserData user) {
		String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
		// String statement = String.format("INSERT INTO users VALUES ('%s', '%s', '%s')", user.username(), hashedPassword, user.email());
		runStatement("INSERT INTO users VALUES (?, ?, ?)", List.of(user.username(), hashedPassword, user.email()));
	}

	public void addAuth(AuthData auth) {
		runStatement("INSERT INTO auths VALUES (?, ?)", List.of(auth.username(), auth.authToken()));
	}

	public void addGame(GameData game) {
		try (var conn = DatabaseManager.getConnection()) {
			try (var preparedStatement = conn.prepareStatement("INSERT INTO games VALUES (?, ?, ?, ?, ?)")) {
				preparedStatement.setInt(1, game.gameID());
				preparedStatement.setString(2, game.whiteUsername());
				preparedStatement.setString(3, game.blackUsername());
				preparedStatement.setString(4, game.gameName());
				preparedStatement.setString(5, serializer.toJson(game.game()));
				preparedStatement.executeUpdate();
			}
		} catch (SQLException | DataAccessException e) {
			throw new InternalServerErrorResponse(e.getMessage());
		}
		/*
		 * runStatement(
				 * "INSERT INTO games VALUES (?, ?, ?, ?, ?)",
				 * List.of(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), (new Gson().newBuilder().enableComplexMapKeySerialization().create()).toJson(game.game())
		 * ));
		 */
	}

	public void overwriteGame(GameData game) {
		try (var conn = DatabaseManager.getConnection()) {
			try (
				var preparedStatement = conn.prepareStatement(
					"UPDATE games SET GameID = ?, WhiteUsername = ?, BlackUsername = ?, GameName = ?, GameObject = ? WHERE GameID = ?"
				)
			) {
				preparedStatement.setInt(1, game.gameID());
				preparedStatement.setString(2, game.whiteUsername());
				preparedStatement.setString(3, game.blackUsername());
				preparedStatement.setString(4, game.gameName());
				preparedStatement.setString(5, serializer.toJson(game.game()));
				preparedStatement.setInt(6, game.gameID());
				preparedStatement.executeUpdate();
			}
		} catch (SQLException | DataAccessException e) {
			throw new InternalServerErrorResponse(e.getMessage());
		}
		/*
		 * runStatement(String.format(
				 * "UPDATE games SET GameID = %d, WhiteUsername = '%s', BlackUsername = '%s', GameName = '%s', GameObject = '%s' WHERE GameID = %d",
				 * game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), (new Gson().newBuilder().enableComplexMapKeySerialization().create()).toJson(game.game()), game.gameID()
		 * ));
		 */
	}

	public void deleteAuth(String authToken) {
		runStatement("DELETE FROM auths WHERE AuthToken = ?", List.of(authToken));
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
