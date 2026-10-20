package server;

import dataaccess.DataAccess;

public class ClearService {
	private final DataAccess da;

	public ClearService(DataAccess access) {
		da = access;
	}

	public DeleteDBResult deleteDB() {
		da.clearAuth();
		da.clearGames();
		da.clearUsers();
		return new DeleteDBResult(true);
	}
}
