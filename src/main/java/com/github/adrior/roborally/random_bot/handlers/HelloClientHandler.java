package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import lombok.NonNull;

public class HelloClientHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(Message message, @NonNull RandomBot client) {
        client.sendMessage(PredefinedClientMessages.helloServer("EdleEisbecher", true, "Version 2.0"));
    }
}
