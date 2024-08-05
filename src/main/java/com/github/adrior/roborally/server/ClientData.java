package com.github.adrior.roborally.server;

import lombok.Data;

/**
 * The ClientData class represents data related to a client in the backend system.
 * This class is annotated with Lombok {@code @Data} annotation, which automatically generates
 * boilerplate code such as getters, setters, {@code toString()}, {@code equals()}, and {@code hashCode()}
 * methods.
 */
@Data
public class ClientData {
    public int clientId;        // The unique identifier for the client.
    public String username;     // The username of the client.
    public int figure = -1;     // The identifier for the client's figure.
    public boolean isAI;        // The flag indicating if the client is a Bot.
}
