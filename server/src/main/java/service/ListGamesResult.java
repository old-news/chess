package service;

import dataobjects.*;
import java.util.Collection;

public record ListGamesResult(Collection<GameData> games) {};
