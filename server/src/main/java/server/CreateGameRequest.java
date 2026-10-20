package server;

public record CreateGameRequest(AuthData auth, String gameName) {};
