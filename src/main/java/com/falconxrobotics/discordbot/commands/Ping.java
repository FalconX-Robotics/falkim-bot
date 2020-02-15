package com.falconxrobotics.discordbot.commands;

import java.time.OffsetDateTime;
import java.util.Arrays;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.CommandGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Ping is a command that is used for checking if the bot is responsive.
 */
public class Ping extends Command {

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public String getPrefix() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Replies with pong";
    }

    @Override
    public String[] getExamples() {
        return new String[] { "" };
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public CommandGroup getParent() {
        return null;
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

        OffsetDateTime msgTime = event.getMessage().getTimeCreated();
        EmbedBuilder builder = new EmbedBuilder().setTitle("Pong!");

        builder.addField("Arguments", Arrays.toString(arguments), false);
        builder.addField("Received Time Delay",
                OffsetDateTime.now().minusNanos(msgTime.getNano()).getNano() + " nano seconds.", false);

        event.getChannel().sendMessage(builder.build()).queue();
    }

}