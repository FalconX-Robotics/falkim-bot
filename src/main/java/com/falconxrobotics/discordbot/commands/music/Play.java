package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.CommandGroup;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Play
 */
public class Play extends Command {

    @Override
    public String getName() {
        return "Play";
    }

    @Override
    public String getPrefix() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays a track.";
    }

    @Override
    public String[] getExamples() {
        return new String[] {"https://youtu.be/ALqOKq0M6ho"};
    }

    @Override
    public String getSyntax() {
        return "[YouTube link]";
    }

    @Override
    public CommandGroup getParent() {
        return Music.getInstance();
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