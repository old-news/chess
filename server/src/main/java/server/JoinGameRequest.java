package server;

public record JoinGameRequest(AuthData auth, String playerColor, int gameID) {};
