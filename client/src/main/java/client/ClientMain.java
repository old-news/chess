package client;

import chess.*;
import ui.*;

public class ClientMain {
	public static void main(String[] args) {
		Tui tui = new Tui();
		while (true) {
			tui.step();
		}
	}
}
