package client;

import chess.*;
import ui.*;
import service.*;
import server.*;

public class ClientMain {
	public static void main(String[] args) {
		String authToken = null;
		ServerFacade sf = new ServerFacade();

		AuthData auth = prelogin(sf);
		GameData game = postlogin(auth, sf);
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

		while (true) {
			tui.step();
			switch (tui.getLastCommand()) {
				case "join" -> {
				}

				case "create" -> {
				}

				case "observe" -> {
				}
				case "logout" -> {
				}
			}
		}
	}
}
