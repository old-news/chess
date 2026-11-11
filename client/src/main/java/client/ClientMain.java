package client;

import chess.*;
import ui.*;
import service.*;
import server.*;

public class ClientMain {
	public static void main(String[] args) {
		ServerFacade sf = new ServerFacade();
		GameData game = null;

		do {
			AuthData auth = prelogin(sf);
			GameData game = postlogin(auth, sf);
		} while (null == game);
	}

	private static AuthData prelogin(ServerFacade sf) {
		PreloginTui pretui = new PreloginTui();

		while (true) {
			pretui.step();
			switch (pretui.getLastCommand()) {
				case "login" -> {
					AuthData res = sf.login(pretui.getLastUserResponse());
					if (null == res) {
						pretui.loginSuccess(false);
					} else {
						pretui.loginSuccess(true);
						return res;
					}
				}
				case "register" -> {
					AuthData res = sf.registerUser(pretui.getLastUserResponse());
					if (null == res) {
						pretui.registerSuccess(false);
					} else {
						pretui.registerSuccess(true);
						return res;
					}
				}
			}
		}
	}

	private static GameData postlogin(AuthData auth, ServerFacade sf) {
		PostloginTui tui = new PostloginTui(auth.username());
		String authToken = auth.authToken();

		while (true) {
			tui.step();
			switch (tui.getLastCommand()) {
				case "create" -> {
					GameData game = sf.createGame(tui.getLastUserResponse());
					tui.createSuccess(null != game);
				}

				case "list" -> {
					Collection<GameData> games = sf.getGames();
					tui.showGames(games);
				}

				case "join" -> {
					GameData game = sf.joinGame(tui.getLastUserResponse());
					if (null == game) {
						tui.joinGameSuccess(false);
					} else {
						tui.joinGameSuccess(true);
						return game;
					}
				}

				case "observe" -> {
					GameData game = sf.loadGame(tui.getLastUserResponse());
					if (null == game) {
						tui.observeGameSuccess(false);
					} else {
						tui.observeGameSuccess(true);
						return game;
					}
				}

				case "logout" -> {
					boolean res = sf.logout(authToken);
					tui.logoutSuccess(res);
					if (res) {
						return null;
					}
				}
			}
		}
	}
}
