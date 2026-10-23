package service;

import dataaccess.DataAccess;

public class ClearService {
	private final DataAccess da;

	public ClearService(DataAccess access) {
		da = access;
	}

	public ClearDBResult clearDB() {
		da.clearAuth();
		da.clearGames();
		da.clearUsers();
		return new ClearDBResult(true);
	}
}
