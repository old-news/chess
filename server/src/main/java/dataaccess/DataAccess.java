package dataaccess;

import service.*;
import java.util.Collection;
import org.mindrot.jbcrypt.BCrypt;

public interface DataAccess {
	public UserData findUser(String username);
	public AuthData findAuth(String authToken);
	public GameData findGame(int gameID);
	public Collection<GameData> getGames();
	public void addUser(UserData user);
	public void addAuth(AuthData auth);
	public void addGame(GameData game);
	public void overwriteGame(GameData game);
	public void deleteAuth(String authToken);
	public void clearUsers();
	public void clearAuth();
	public void clearGames();
	// public static boolean validatePassword(String plainTextPassword, String passwordHash);

	public static boolean validatePassword(String plainTextPassword, String passwordHash) {
		return BCrypt.checkpw(plainTextPassword, passwordHash);
	}
}
