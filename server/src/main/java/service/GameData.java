package service;

import chess.ChessGame;

import com.google.gson.Gson;

public record GameData(
	int gameID,
	String whiteUsername,
	String blackUsername,
	String gameName,
	ChessGame game
) {
	GameData(int gi, String wu, String bu, String gn, String jsonGame) {
		this(gi, wu, bu, gn, (new Gson()).fromJson(jsonGame, ChessGame.class));
	}
};
