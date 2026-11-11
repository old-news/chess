package client;

import chess.*;
import ui.*;

public class ClientMain {
	public static void main(String[] args) {
		Tui tui = new Tui();
		while (true) {
			tui.step();
			switch (tui.getLastCommand()) {
				case "login" -> {
					System.out.println("logging in with: " + tui.getLastUserResponse());
					// server.login(tui.getLastUserResponse());
				}
				case "register" -> {
					System.out.println("registering with: " + tui.getLastUserResponse());
					// server.registerUser(tui.getLastUserResponse());
				}
			}
		}
	}
}
