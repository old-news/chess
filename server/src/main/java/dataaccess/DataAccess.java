package dataaccess;

import service.*;
import java.util.Collection;

public interface DataAccess {
	public UserData findUser(String username);
	public AuthData findAuth(String authToken);
	public GameData findGame(int gameID);
	public Collection<GameData> getGames();
	public void addUser(UserData user);
	public void addGame(GameData game);
	public void addAuth(AuthData auth);
	public void overwriteGame(GameData game);
	public void deleteAuth(String authToken);
	public void clearUsers();
	public void clearGames();
	public void clearAuth();
}
