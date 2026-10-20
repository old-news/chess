package dataaccess;

import server.*;
import java.util.Collection;

public interface DataAccess {
	public UserData getUser(String username);
	public UserData findUser(String username);
	public AuthData findAuth(String authToken);
	public Collection<GameData> getGames();
	public GameData findGame(int gameID);
	public void addUser(UserData user);
	public void addGame(GameData game);
	public void addAuth(AuthData auth);
	public void overwriteGame(GameData game);
	public bool deleteAuth(String authToken);
	public void clearUsers();
	public void clearGames();
	public void clearAuth();

}
