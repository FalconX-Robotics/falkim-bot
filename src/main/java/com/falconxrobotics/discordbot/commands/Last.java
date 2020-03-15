package com.falconxrobotics.discordbot.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Ping is a command that is used for checking if the bot is responsive.
 */
public class Last extends Command {

    private Map<Long, ArrayList<MessageReceivedEvent>> lastMessageEvents = new HashMap<Long, ArrayList<MessageReceivedEvent>>();

    public Last() {
        super("Last", "last");
        setDescription("Gets the last messages sent in the channel, including deleted messages.");
        addExamples("2", "2 4", "");
        setSyntax("[optional: number of messages; max: 50] OR [start] [end]");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        long channelId = event.getChannel().getIdLong();
        ArrayList<MessageReceivedEvent> pastEvents = lastMessageEvents.getOrDefault(channelId, new ArrayList<MessageReceivedEvent>(11));
        pastEvents.add(0, event);
        lastMessageEvents.put(channelId, pastEvents);
        if (lastMessageEvents.get(channelId).size() > 50) {
            lastMessageEvents.get(event.getChannel().getIdLong()).remove(lastMessageEvents.size() - 1);
        }

        for (MessageReceivedEvent e : pastEvents) {
            System.out.println(e.getMessage().getContentDisplay());
        }
        
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        String[] args = splitUserInput(messageContent);
        try {
            int start = 0;
            int end = 1;
            if (args.length == 1) {
                end = Integer.parseInt(args[0]);
            } else if (args.length > 1) {
                start = Integer.parseInt(args[0]);
                end = Integer.parseInt(args[1]);
            }
            if (start < 0 || end < 0) {
                event.getChannel().sendMessage(getEmbedInvalidParameterError("Arguments cannot be less than 1").build())
                        .queue();
            } else if (end > 50) {
                event.getChannel()
                        .sendMessage(getEmbedInvalidParameterError("Argument cannot be bigger than than 50").build())
                        .queue();
            } else if (lastMessageEvents.get(channelId).size() < 2) {
                event.getChannel()
                        .sendMessage("No previous messages recorded")
                        .queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder()
                    .setColor(Color.ORANGE);
                for (int i = end; i >= start && i < lastMessageEvents.get(channelId).size() && i != 0; i--) {
                    MessageReceivedEvent lastEvent = lastMessageEvents.get(channelId).get(i);
                    if (lastEvent != null)
                        builder.addField(
                                i + ". " + lastEvent.getAuthor().getName() + "#"
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