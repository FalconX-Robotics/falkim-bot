package com.falconxrobotics.discordbot.commands;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Ping is a command that is used for checking if the bot is responsive.
 */
public class Last extends Command {

    private Map<Long, ArrayDeque<MessageReceivedEvent>> lastMessageEvents = new HashMap<Long, ArrayDeque<MessageReceivedEvent>>();

    public Last() {
        super("Last", "last");
        setDescription("Gets the last messages sent in the channel, including deleted messages.");
        addExamples("2", "");
        setSyntax("[optional: number of messages; max: 10]");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        long channelId = event.getChannel().getIdLong();
        ArrayDeque<MessageReceivedEvent> pastEvents = lastMessageEvents.getOrDefault(channelId, new ArrayDeque<MessageReceivedEvent>(11));
        pastEvents.push(event);
        lastMessageEvents.put(channelId, pastEvents);
        if (lastMessageEvents.get(channelId).size() > 10) {
            lastMessageEvents.get(event.getChannel().getIdLong()).removeLast();
        }

        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        String[] args = splitUserInput(messageContent);
        try {
            int n = args.length < 1 ? 1 : Integer.parseInt(args[0]);
            if (n < 0) {
                event.getChannel().sendMessage(getEmbedInvalidParameterError("Argument cannot be less than 1").build())
                        .queue();
            } else if (n > 10) {
                event.getChannel()
                        .sendMessage(getEmbedInvalidParameterError("Argument cannot be bigger than than 10").build())
                        .queue();
            } else if (lastMessageEvents.get(channelId).size() < 2) {
                event.getChannel()
                        .sendMessage("No previous messages recorded")
                        .queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder()
                    .setColor(Color.ORANGE);
                Iterator<MessageReceivedEvent> iter = lastMessageEvents.get(channelId).iterator();
                iter.next();
                for (int i = 0; i < n && iter.hasNext(); i++) {
                    MessageReceivedEvent lastEvent = iter.next();
                    if (lastEvent != null)
                        builder.addField(
                                i + 1 + ". " + lastEvent.getAuthor().getName() + "#"
                                        + lastEvent.getAuthor().getDiscriminator() + ":",
                                lastEvent.getMessage().getContentDisplay(), false);
                }

                event.getChannel().sendMessage(builder.build()).queue();
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            event.getChannel().sendMessage(getEmbedInvalidParameterTypes().build()).queue();
        }
    }

}