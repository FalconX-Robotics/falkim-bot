package com.falconxrobotics.discordbot.commands;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Rainbow extends Command {

    private String[] emojis = new String[] {"🟥", "🟧", "🟨", "🟩", "🟦", "🟪"};
    
    public Rainbow() {
        super("Rainbow", "rainbow");
        setDescription("Rainbow.");
        addExamples("");
        setSyntax("");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        for (int i = 0; i < emojis.length; i++) {
            event.getMessage().addReaction(emojis[i]).queue();
        }
    }

}