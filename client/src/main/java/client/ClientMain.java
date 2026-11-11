package client;

import chess.*;
import ui.*;
import service.*;
import server.*;

import java.util.List;

public class ClientMain {
	public static void main(String[] args) {
		ServerFacade sf = new ServerFacade();
		GameData game = null;

		while (true) {
			AuthData auth = prelogin(sf);
			game = postlogin(auth, sf);
			if (null == game) continue;
		}
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
					List<String> req = List.of(authToken, tui.getLastUserResponse().get(0));
					GameData game = sf.createGame(req);
					if (null == game) {
						tui.createSuccess(null);
					} else {
						tui.createSuccess(game.gameName());
					}
				}

				case "list" -> {
					List<GameData> games = sf.listGames(authToken);
					tui.showGames(games);
				}

				case "join" -> {
					List<String> resp = tui.getLastUserResponse();
					List<String> req = List.of(authToken, resp.get(0), resp.get(1));
					GameData game = sf.joinGame(req);
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
