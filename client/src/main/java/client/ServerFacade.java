package client;

import server.*;
import service.*;
import dataaccess.*;
import dataaccess.sql.*;

import java.util.List;

public class ServerFacade {
	private DataAccess da;
	private UserService us;

	public ServerFacade() {
		da = new SQLAccess();
		us = new UserService(da);
	}

	public void run(int n) {
		return;
	}

	public void stop() {
		return;
	}

	public AuthData registerUser(List<String> data) {
		try {
			RegisterResult res = us.registerUser(new RegisterRequest(data.get(0), data.get(1), data.get(2)));
			return res.auth();
		} catch (DataAccessException e) {
			return null;
		}
	}

	public AuthData login(List<String> data) {
		try {
			LoginResult res = us.login(new LoginRequest(data.get(0), data.get(1)));
			return res.auth();
		} catch (DataAccessException e) {
			return null;
		}
	}
}
