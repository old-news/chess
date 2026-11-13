package ui;

import java.util.Scanner;
import java.util.List;

public class PreloginTui {
	private static Scanner input;
	private static String username;
	private List<String> lastUserResponse;
	private String lastCommand;

	public PreloginTui () {
		input = new Scanner(System.in);
		username = EscapeSequences.SET_TEXT_COLOR_RED + "LOGGED_OUT" + EscapeSequences.RESET_TEXT_COLOR;
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
			case "register" -> {
				commandRegister();
			}
			case "login" -> {
				commandLogin();
			}
			default -> {
				commandDoesNotExist(command);
			}
		}
		lastCommand = command.toLowerCase();
	}

	private static void commandHelp() {
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "register" + EscapeSequences.RESET_TEXT_COLOR +
			" - create an account"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_YELLOW + "login" + EscapeSequences.RESET_TEXT_COLOR +
			" - login to an existing account"
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

	private static void commandQuit() {
		System.exit(0);
	}

	private static void commandDoesNotExist(String command) {
		System.out.println("\t'" + command + "' is an invalid command");
		System.out.println("\tBelow are the available commands:");
		commandHelp();
	}

	private void commandRegister() {
		String username = null;
		do {
			if (null != username) {
				System.out.println("You must submit a username that's at least 1 character long");
			}
			System.out.print("\tUsername: ");
			username = input.nextLine();
		} while (username.isEmpty());
		System.out.print("\tPassword: ");
		String password = input.nextLine();
		System.out.print("\tEmail: ");
		String email = input.nextLine();
		lastUserResponse = List.of(username, password, email);
	}

	private void commandLogin() {
		System.out.print("\tUsername: ");
		String username = input.nextLine();
		System.out.print("\tPassword: ");
		String password = input.nextLine();
		lastUserResponse = List.of(username, password);
	}

	public String getLastCommand() {
		return lastCommand;
	}

	public List<String> getLastUserResponse() {
		return lastUserResponse;
	}

	public void loginSuccess(boolean successful) {
		if (successful) {
			username = EscapeSequences.SET_TEXT_COLOR_BLUE + lastUserResponse.getFirst() + EscapeSequences.RESET_TEXT_COLOR;
			System.out.println("You're all logged in " + username + "!");
		} else {
			System.out.println("Login failed");
		}
	}

	public void registerSuccess(boolean successful) {
		if (successful) {
			username = EscapeSequences.SET_TEXT_COLOR_BLUE + lastUserResponse.getFirst() + EscapeSequences.RESET_TEXT_COLOR;
			System.out.println("You're registered and logged in " + username + "!");
		} else {
			System.out.println("Registration failed - try a different username");
		}
	}

	public void error(String msg) {
		System.out.println("Previous command failed due to " + msg);
	}

	public static void greet() {
		System.out.println("Chess client - type 'help' for options");
	}
}
