package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Play
 */
public class Play extends Command {

    public Play() {
        super("Play", "play");
        setDescription("Plays a track.");
        addExamples("https://youtu.be/ALqOKq0M6ho");
        setSyntax("[YouTube link]");
        setParent(Music.getInstance());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        String[] arguments = splitUserInput(messageContent);

        if (arguments.length == 0) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
        }
    }
    
}