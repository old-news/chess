package ui;

import service.*;

import java.util.Scanner;
import java.util.List;

public class PostloginTui {
	private static Scanner input;
	private static String username;
	private static String plainUsername;
	private List<String> lastUserResponse;
	private String lastCommand;

	public PostloginTui (String user) {
		input = new Scanner(System.in);
		plainUsername = user;
		username = EscapeSequences.SET_TEXT_COLOR_BLUE + user + EscapeSequences.RESET_TEXT_COLOR;
		lastUserResponse = null;
		lastCommand = null;
	}

	public void step() {
		System.out.print(username + " >> ");
		String command = input.nextLine();
		runCommand(command);
	}

	private void runCommand(String command) {
		switch (command.toLowerCase()) {
			case "help" -> {
				commandHelp();
			}
			case "quit" -> {
				commandQuit();
			}
			case "create" -> {
				commandCreate();
			}
			case "list" -> {
				commandList();
			}
			case "join" -> {
				commandJoin();
			}
			case "observe" -> {
				commandObserve();
			}
			case "logout" -> {
				commandLogout();
			}
			default -> {
				commandDoesNotExist(command);
			}
		}
		lastCommand = command.toLowerCase();
	}

	private static void commandHelp() {
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "create" + EscapeSequences.RESET_TEXT_COLOR +
			" - create a game"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "list" + EscapeSequences.RESET_TEXT_COLOR +
			" - list all games"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "join" + EscapeSequences.RESET_TEXT_COLOR +
			" - join a game"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "observe" + EscapeSequences.RESET_TEXT_COLOR +
			" - observe a game"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "logout" + EscapeSequences.RESET_TEXT_COLOR +
			" - logout of the server"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "quit" + EscapeSequences.RESET_TEXT_COLOR +
			" - exit the program"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "help" + EscapeSequences.RESET_TEXT_COLOR +
			" - display this message"
		);
	}

	private void commandCreate() {
		String name = null;
		do {
			if (null != name) {
				System.out.println("You must submit a name that has at least 1 character");
			}
			System.out.print("\tGame Name: ");
			name = input.nextLine();
		} while (name.isEmpty());
		lastUserResponse = List.of(name);
	}

	private void commandList() {
		return;
	}

	private void commandJoin() {
		String number = getGameNumber();
		String color = getGameColor();
		lastUserResponse = List.of(color, number);
	}

	private String getGameNumber() {
		String number = null;
		do {
			if (null != number) {
				System.out.println("You must submit a numeric Game #");
			}
			System.out.print("\tGame #: ");
			number = input.nextLine();
		} while (!number.matches("\\d+"));
		return number;
	}

	private String getGameColor() {
		String color = null;
		do {
			if (null != color) {
				System.out.println("You must submit 'w' (white) or 'b' (black) as your color");
			}
			System.out.print("\tColor (w/b): ");
			color = input.nextLine();
		} while (!color.matches("[wWbB]"));
		if ("w".equals(color.toLowerCase())) {
			color = "WHITE";
		} else {
			color = "BLACK";
		}
		return color;
	}

	private void commandObserve() {
		String number = getGameNumber();
		String color = getGameColor();
		lastUserResponse = List.of(color, number);
	}

	private void commandLogout() {
		return;
	}
	private static void commandQuit() {
		return;
	}

	private static void commandDoesNotExist(String command) {
		System.out.println("\t'" + command + "' is an invalid command");
		System.out.println("\tBelow are the available commands:");
		commandHelp();
	}

	public String getLastCommand() {
		return lastCommand;
	}

	public List<String> getLastUserResponse() {
		return lastUserResponse;
	}

	public void createSuccess(String name) {
		if (null == name) {
			System.out.println("Failed to create game");
		} else {
			System.out.println(
				"Game " + EscapeSequences.SET_TEXT_COLOR_GREEN + name + EscapeSequences.RESET_TEXT_COLOR +
				" successfully created"
			);
		}
	}

	public void showGames(List<GameData> games) {
		String nobody = EscapeSequences.SET_TEXT_COLOR_RED + "Nobody" + EscapeSequences.RESET_TEXT_COLOR;
		for (int i = 0; i < games.size(); i++) {
			GameData game = games.get(i);
			String white = EscapeSequences.SET_TEXT_COLOR_MAGENTA + game.whiteUsername() + EscapeSequences.RESET_TEXT_COLOR;
			String black = EscapeSequences.SET_TEXT_COLOR_MAGENTA + game.blackUsername() + EscapeSequences.RESET_TEXT_COLOR;
			System.out.println(
				"\t" + (i+1) + ": " +
				EscapeSequences.SET_TEXT_COLOR_GREEN + game.gameName() + EscapeSequences.RESET_TEXT_COLOR + " with " +
				((null == game.whiteUsername()) ? nobody : white) + " VS " +
				((null == game.blackUsername()) ? nobody : black)
			);
		}
	}

	public void joinGameSuccess(boolean succeeded) {
		if (!succeeded) {
			System.out.println("Failed to join game - enter a valid game number/color");
		} else {
			System.out.println("Joined game successfully!");
		}
	}

	public void observeGameSuccess(boolean succeeded) {
		if (!succeeded) {
			System.out.println("Failed to observe game");
		} else {
			System.out.println("Observing successfully");
		}
	}

	public void logoutSuccess(boolean succeeded) {
		if (!succeeded) {
			System.out.println("Failed to logout");
		} else {
			System.out.println("Successfully logged out");
		}
	}

	public void internalError(String code) {
		System.out.println("Previous command failed due to server error, ERR CODE: " + code);
	}
}
