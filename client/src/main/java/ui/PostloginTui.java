package ui;

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
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "create" + EscapeSequences.RESET_TEXT_COLOR +
			" - create a game"
		);
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "list" + EscapeSequences.RESET_TEXT_COLOR +
			" - list all games"
		);
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "join" + EscapeSequences.RESET_TEXT_COLOR +
			" - join a game"
		);
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "observe" + EscapeSequences.RESET_TEXT_COLOR +
			" - observe a game"
		);
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "logout" + EscapeSequences.RESET_TEXT_COLOR +
			" - logout of the server"
		);
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "quit" + EscapeSequences.RESET_TEXT_COLOR +
			" - exit the program"
		);
		System.out.println("\t\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "help" + EscapeSequences.RESET_TEXT_COLOR +
			" - display this message"
		);
	}

	private void commandCreate() {
		System.exit(0);
	}

	private void commandList() {
		System.exit(0);
	}

	private void commandJoin() {
		System.exit(0);
	}

	private void commandObserve() {
		System.exit(0);
	}

	private void commandLogout() {
		return;
	}
	private static void commandQuit() {
		System.exit(0);
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
}
