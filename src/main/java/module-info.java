module com.github.adrior.roborally {
    requires com.google.gson;
    requires java.logging;
    requires static lombok;

    exports com.github.adrior.roborally.server;
    exports com.github.adrior.roborally.server.util;
    exports com.github.adrior.roborally.client;
    exports com.github.adrior.roborally.message;
    exports com.github.adrior.roborally.utility;
    exports com.github.adrior.roborally.commands;
    exports com.github.adrior.roborally.exceptions;
    exports com.github.adrior.roborally.core.player;
    exports com.github.adrior.roborally.core.game;
    exports com.github.adrior.roborally.core.game.recorder;
    exports com.github.adrior.roborally.core.game.util;
    exports com.github.adrior.roborally.core.tile;
    exports com.github.adrior.roborally.core.tile.tiles;
    exports com.github.adrior.roborally.core.map;
    exports com.github.adrior.roborally.core.map.adapters;
    exports com.github.adrior.roborally.core.map.parsers;
    exports com.github.adrior.roborally.core.map.data;
    exports com.github.adrior.roborally.core.card;
    exports com.github.adrior.roborally.core.card.cards;
    exports com.github.adrior.roborally.message.utils;
    exports com.github.adrior.roborally.message.handlers;

    opens com.github.adrior.roborally.core.tile to com.google.gson;
    opens com.github.adrior.roborally.core.map to com.google.gson;
    opens com.github.adrior.roborally.core.card to com.google.gson;
    opens com.github.adrior.roborally.core.game to com.google.gson;
    opens com.github.adrior.roborally.core.player to com.google.gson;
    opens com.github.adrior.roborally.core.tile.tiles to com.google.gson;
    opens com.github.adrior.roborally.core.map.adapters to com.google.gson;
    opens com.github.adrior.roborally.core.map.parsers to com.google.gson;
    opens com.github.adrior.roborally.core.map.data to com.google.gson;
    opens com.github.adrior.roborally.utility to com.google.gson;
    opens com.github.adrior.roborally.exceptions to com.google.gson;
    opens com.github.adrior.roborally.core.game.recorder to com.google.gson;
    opens com.github.adrior.roborally.core.card.cards to com.google.gson;
    opens com.github.adrior.roborally.core.game.util to com.google.gson;
}