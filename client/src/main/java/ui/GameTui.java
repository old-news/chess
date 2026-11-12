package ui;

import service.*;
import chess.*;

import java.util.Scanner;
import java.util.List;

public class GameTui {
	private static Scanner input;
	private static String username;
	private static String plainUsername;
	private List<String> lastUserResponse;
	private String lastCommand;

	public GameTui(String user) {
		input = new Scanner(System.in);
		plainUsername = user;
		username = EscapeSequences.SET_TEXT_COLOR_BLUE + user + EscapeSequences.RESET_TEXT_COLOR;
		lastUserResponse = null;
		lastCommand = null;
	}

	public void step() {
		printCommandPrompt();
		String command = input.nextLine();
		runCommand(command);
	}

	private static void printCommandPrompt() {
		System.out.print(username + " >> ");
	}

	private void runCommand(String command) {
		switch (command.toLowerCase()) {
			case "help" -> {
				commandHelp();
			}
			case "quit" -> {
				commandQuit();
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
			EscapeSequences.SET_TEXT_COLOR_GREEN + "create" + EscapeSequences.RESET_TEXT_COLOR +
			" - create a game"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "list" + EscapeSequences.RESET_TEXT_COLOR +
			" - list all games"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "join" + EscapeSequences.RESET_TEXT_COLOR +
			" - join a game"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "observe" + EscapeSequences.RESET_TEXT_COLOR +
			" - observe a game"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "logout" + EscapeSequences.RESET_TEXT_COLOR +
			" - logout of the server"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "quit" + EscapeSequences.RESET_TEXT_COLOR +
			" - exit the program"
		);
		System.out.println("\t" +
			EscapeSequences.SET_TEXT_COLOR_GREEN + "help" + EscapeSequences.RESET_TEXT_COLOR +
			" - display this message"
		);
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

	public void internalError() {
		System.out.println("Previous command failed due to server error");
	}

	public void display(GameData game) {
		System.out.print(EscapeSequences.ERASE_SCREEN);
		boolean isWhite = null == game.blackUsername() || !(game.blackUsername().equals(plainUsername));
		String against = EscapeSequences.SET_TEXT_COLOR_RED + "Nobody" + EscapeSequences.RESET_TEXT_COLOR;
		if (isWhite) {
			if (null != game.blackUsername()) {
				against = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + game.blackUsername() + EscapeSequences.RESET_TEXT_COLOR;
			}
		} else {
			if (null != game.whiteUsername()) {
				against = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + game.whiteUsername() + EscapeSequences.RESET_TEXT_COLOR;
			}
		}
		System.out.println("VS: " + against);
		String letters = "  abcdefgh";
		if (!isWhite) {
			letters = "  hgfedcba";
		}
		String numbers = "87654321";
		if (!isWhite) {
			numbers = "12345678";
		}
		ChessBoard board = game.game().getBoard();
		String boardStr = board.toString();
		String[] boardRows = boardStr.split("\n");
		System.out.println(letters);
		for (int row = 0; row < 8; row++) {
			int rowi = isWhite ? row : 7 - row;
			String rowstr = boardRows[rowi];
			System.out.print(numbers.charAt(row) + " ");
			for (int col = 0; col < 8; col++) {
				int coli = isWhite ? col : 7 - col;
				boolean blackBG = ((coli + rowi) % 2 == 0);
				String bgcolor = blackBG ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
				String fgcolor;
				char piece = rowstr.charAt(coli);
				if (piece < 96 && isWhite || piece > 96 && !isWhite) {
					fgcolor = EscapeSequences.SET_TEXT_COLOR_BLUE;
				} else {
					fgcolor = blackBG ? EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY : EscapeSequences.SET_TEXT_COLOR_DARK_GREY;
					if (null == game.whiteUsername() || null == game.blackUsername()) {
						fgcolor = EscapeSequences.SET_TEXT_COLOR_RED;
					}
				}
				System.out.print(bgcolor + fgcolor + piece + EscapeSequences.RESET_BG_COLOR);
				// System.out.print("x ");
			}
			System.out.println(EscapeSequences.RESET_TEXT_COLOR + " " + numbers.charAt(row));
		}
		System.out.println(letters);
		printCommandPrompt();
	}
}
