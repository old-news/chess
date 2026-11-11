package ui;

import java.util.Scanner;

public class Tui {
	private static Scanner input;
	private static String username;

	public Tui () {
		input = new Scanner(System.in);
		username = EscapeSequences.SET_TEXT_COLOR_RED + "LOGGED_OUT" + EscapeSequences.RESET_TEXT_COLOR;
		System.out.println("Chess client - type 'help' for options");
	}

	public static void step() {
		System.out.print(username + " >> ");
		String command = input.nextLine();
		runCommand(command);
	}

	private static String runCommand(String command) {
		switch (command.toLowerCase()) {
			case "help" -> {
				commandHelp();
			}
			case "quit" -> {
				commandQuit();
			}
			case "register" -> {
				return commandRegister();
			}
			case "login" -> {
				return commandLogin();
			}
			default -> {
				commandDoesNotExist(command);
			}
		}
		return null;
	}

	private static void commandHelp() {
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "register" + EscapeSequences.RESET_TEXT_COLOR +
			" - create an account"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "login" + EscapeSequences.RESET_TEXT_COLOR +
			" - login to an existing account"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "exit" + EscapeSequences.RESET_TEXT_COLOR +
			" - exit the program"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "help" + EscapeSequences.RESET_TEXT_COLOR +
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

	private static String commandRegister() {
		System.out.print("\tUsername: ");
		String username = input.nextLine();
		System.out.print("\tPassword: ");
		String password = input.nextLine();
		System.out.print("\tEmail: ");
		String email = input.nextLine();
		return String.format("{username=%s, password=%s, email=%s}", username, password, email);
	}

	private static String commandLogin() {
		System.out.print("\tUsername: ");
		String username = input.nextLine();
		System.out.print("\tPassword: ");
		String password = input.nextLine();
		return String.format("{username=%s, password=%s}", username, password);
	}
}
