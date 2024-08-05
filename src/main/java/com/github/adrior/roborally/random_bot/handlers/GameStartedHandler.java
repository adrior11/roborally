package com.github.adrior.roborally.random_bot.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.utility.GsonUtil;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.List;

public class GameStartedHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        // Deserialize the gameMap field back to List<List<List<Tile>>>.
        JsonObject messageBody = GsonUtil.getGson().fromJson(GsonUtil.getGson().toJson(message.messageBody()), JsonObject.class);
        JsonElement gameMapElement = messageBody.get("gameMap");
        Type mapType = new TypeToken<List<List<List<Tile>>>>() {}.getType();
        List<List<List<Tile>>> gameMap = GsonUtil.getGson().fromJson(gameMapElement, mapType);

        client.log("Game started!");

        client.setMap(gameMap);
    }
}
