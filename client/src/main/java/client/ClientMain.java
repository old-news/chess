package client;

import chess.*;
import ui.*;
import service.*;
import server.*;

import io.javalin.http.InternalServerErrorResponse;
import java.util.List;

public class ClientMain {
	public static void main(String[] args) {
		Client c = new Client();
		c.run();
	}
}
