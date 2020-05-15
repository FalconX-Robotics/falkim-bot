package com.falconxrobotics.discordbot.commands.moderation;

import java.util.concurrent.TimeUnit;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Purge extends Command {

    public Purge() {
        super("Purge", "purge");
        setDescription("Removes a number of messages in a channel.");
        addExamples("10");
        setSyntax("[number of messages to delete; max: 99]");
    }

    private void purge(TextChannel channel, int count) {
        channel.deleteMessages(channel.getHistory().retrievePast(100).complete().subList(0, count + 1))
                .queue(m -> channel.sendMessage("Purged.").queue(m2 -> m2.delete().queueAfter(2, TimeUnit.SECONDS)));
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

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)
                && !(event.getAuthor().getIdLong() == 239501588589182987L)) {
            event.getChannel().sendMessage(
                    getEmbedSimpleError("Permission Not Met", "You must have ``manage messages`` permission.").build())
                    .queue();
            return;
        }

        if (arguments.length < 1) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {

            try {
                int count = Integer.parseUnsignedInt(arguments[0]);
                if (count > 99) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterError("Max Count Exceeded").build()).queue();
                    return;
                }

                purge(event.getTextChannel(), count);
            } catch (NumberFormatException nfe) {
                event.getChannel().sendMessage(getEmbedInvalidParameterTypes().build()).queue();
                return;
            } catch (Exception e) {
                event.getChannel().sendMessage(getEmbedSimpleError("Permission Error",
                        "The bot does not have sufficient permission to delete messages.").build()).queue();
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public EmbedBuilder getEmbedInfo() {
        EmbedBuilder builder = super.getEmbedInfo();
        builder.addField("Time Parameter", "[number][s OR h OR d]", false);
        builder.addField("Time Types", "``s`` means seconds\n``m`` is minutes\n``h`` means hours\n``d`` means days.",
                false);
        return builder;
    }
}