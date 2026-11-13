package client;

import chess.*;
import ui.*;
import service.*;
import server.*;

import io.javalin.http.InternalServerErrorResponse;
import java.util.List;

public class Client {
	private ServerFacade sf;
	private GameData game;
	private boolean loggedIn;
	private boolean listedGames;
	private boolean observing;
	private AuthData auth;
	private ChessGame.TeamColor color;

	public Client() {
		sf = new ServerFacade(8080);
		game = null;
		loggedIn = false;
		listedGames = false;
		observing = false;
		auth = null;
		color = ChessGame.TeamColor.WHITE;
	}

	public void run() {
		greet();
		while (true) {
			listedGames = false;
			observing = false;
			if (!loggedIn) {
				prelogin();
			}
			loggedIn = true;
			game = postlogin();
			if (null == game) {
				loggedIn = false;
				continue;
			}
			play();
		}
	}

	private void greet() {
		PreloginTui.greet();
	}

	private void prelogin() {
		PreloginTui pretui = new PreloginTui();

		while (true) {
			pretui.step();
			try {
				auth = managePreloginCommand(pretui.getLastCommand(), pretui);
				if (null != auth) {
					return;
				}
			} catch (RuntimeException e) {
				pretui.error(e.getMessage());
			}
		}
	}

	private AuthData managePreloginCommand(String command, PreloginTui pretui) {
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

	private GameData postlogin() {
		PostloginTui tui = new PostloginTui(auth.username());

		while (true) {
			tui.step();
			try {
				GameData game = managePostloginCommand(tui.getLastCommand(), tui);
				if (null != game) {
					if (null == game.game()) {
						return null;
					}
					return game;
				}
			} catch (RuntimeException e) {
				tui.error(e.getMessage());
			}
		}
	}

	private GameData managePostloginCommand(String command, PostloginTui tui) {
		String authToken = auth.authToken();
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
				listedGames = true;
			}

			case "join" -> {
				List<String> resp = tui.getLastUserResponse();
				List<String> req = List.of(authToken, resp.get(0), resp.get(1));
				GameData game = sf.joinGame(req);
				if (null == game) {
					tui.joinGameSuccess(false);
				} else {
					tui.joinGameSuccess(true);
					color = (tui.getLastUserResponse().get(0).equals("WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
					return game;
				}
			}

			case "observe" -> {
				GameData game = sf.loadGame(List.of(authToken, tui.getLastUserResponse().get(1)));
				if (null == game) {
					tui.observeGameSuccess(false);
				} else {
					tui.observeGameSuccess(true);
					color = (tui.getLastUserResponse().get(0).equals("WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
					observing = true;
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

	private void play() {
		GameTui tui = new GameTui(auth.username());
		tui.display(game, color, observing);
		while (true) {
			GameData loadedGame = sf.getGame(game.gameID());
			if (!(game.equals(loadedGame))) {
				game = loadedGame;
				tui.display(game, color, observing);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
}
