package client;

import chess.*;
import ui.*;
import service.*;
import server.*;

import io.javalin.http.InternalServerErrorResponse;
import java.util.List;

public class ClientMain {
	public static void main(String[] args) {
		ServerFacade sf = new ServerFacade();
		GameData game = null;
		boolean loggedIn = false;
		AuthData auth = null;
		greet();

		while (true) {
			if (!loggedIn) {
				auth = prelogin(sf);
			}
			loggedIn = true;
			game = postlogin(auth, sf);
			if (null == game) {
				loggedIn = false;
				continue;
			}
			play(game, auth, sf);
		}
	}

	private static void greet() {
		PreloginTui.greet();
	}

	private static AuthData prelogin(ServerFacade sf) {
		PreloginTui pretui = new PreloginTui();

		while (true) {
			pretui.step();
			try {
				AuthData auth = managePreloginCommand(pretui.getLastCommand(), sf, pretui);
				if (null != auth) {
					return auth;
				}
			} catch (InternalServerErrorResponse e) {
				pretui.internalError();
			}
		}
	}

	private static AuthData managePreloginCommand(String command, ServerFacade sf, PreloginTui pretui) {
		switch (command) {
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
		return null;
	}

	private static GameData postlogin(AuthData auth, ServerFacade sf) {
		PostloginTui tui = new PostloginTui(auth.username());
		String authToken = auth.authToken();

		while (true) {
			tui.step();
			try {
				GameData game = managePostloginCommand(tui.getLastCommand(), sf, tui, authToken);
				if (null != game) {
					if (null == game.game()) {
						return null;
					}
					return game;
				}
			} catch (InternalServerErrorResponse e) {
				tui.internalError();
			}
		}
	}

	private static GameData managePostloginCommand(String command, ServerFacade sf, PostloginTui tui, String authToken) {
		switch (command) {
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
					return new GameData(0, null, null, null, null);
				}
			}

			case "quit" -> {
				boolean res = sf.logout(authToken);
				tui.logoutSuccess(res);
				System.exit(0);
				if (res) {
					return null;
				}
			}
		}
		return null;
	}

	private static void play(GameData game, AuthData auth, ServerFacade sf) {
		return;
	}
}
