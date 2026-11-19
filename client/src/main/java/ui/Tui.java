package ui;

import dataobjects.*;

import java.util.Scanner;
import java.util.List;

public abstract class Tui {
	protected static Scanner input;
	protected static String username;
	protected static String plainUsername;
	protected List<String> lastUserResponse;
	protected String lastCommand;

	public Tui (String user) {
		input = new Scanner(System.in);
		plainUsername = user;
		username = EscapeSequences.SET_TEXT_COLOR_BLUE + user + EscapeSequences.RESET_TEXT_COLOR;
		lastUserResponse = null;
		lastCommand = null;
	}

	public Tui () {
		input = new Scanner(System.in);
		username = EscapeSequences.SET_TEXT_COLOR_RED + "LOGGED_OUT" + EscapeSequences.RESET_TEXT_COLOR;
		plainUsername = null;
		lastUserResponse = null;
		lastCommand = null;
	}

	public void step() {
		printCommandPrompt();
		String command = input.nextLine();
		runCommand(command);
	}

	protected static void printCommandPrompt() {
		System.out.print(username + " >> ");
	}

	protected void commandDoesNotExist(String command) {
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

	public void error(String msg) {
		System.out.println("Previous command failed due to " + msg);
	}

	protected abstract void runCommand(String command);
	protected abstract void commandHelp();
}
