package com.falconxrobotics.discordbot.commands;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Pray
 */
public class Pray extends Command {

    public Pray() {
        super("Pray", "pray");
        setDescription("Prays to Allah.");
        setSyntax("");
        addExamples("");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;
        
        event.getChannel().sendMessage(new EmbedBuilder().setDescription("الله أكبر").build()).queue();
    }
}