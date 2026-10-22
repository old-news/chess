package server;

import java.util.Map;

public record HandlerResult(int status, Map<String, Object> body) {};
