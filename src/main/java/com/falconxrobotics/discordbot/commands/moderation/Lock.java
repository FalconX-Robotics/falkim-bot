package com.falconxrobotics.discordbot.commands.moderation;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Lock extends Command {

    public Lock() {
        super("Lock", "lock");
        setDescription("Locks a channel and prevents members from talking.");
        addExamples("#chat");
        setSyntax("[optional: channels]");

    }

    private void lock(TextChannel channel) {
        // channel.putPermissionOverride(channel.getGuild().getRole).deny(Permission.MESSAGE_WRITE).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // String messageContent = event.getMessage().getContentDisplay();
        // if (event.getAuthor().isBot())
        //     return;
        // if (!getInputValidity(messageContent))
        //     return;

        // messageContent = trimInputBeginning(messageContent);
        // String[] arguments = splitUserInput(messageContent);

        // if (!event.getMember().hasPermission(Permission.KICK_MEMBERS) && !(event.getAuthor().getIdLong() == 239501588589182987L)) {
        //     event.getChannel().sendMessage(getEmbedSimpleError("Permission Not Met", "You must have ``kick member`` permission.").build()).queue();
        //     return;
        // }

        // List<TextChannel> channels = event.getMessage().getMentionedChannels();
        // if (!channels.isEmpty()) {

        // } else {
        //     lock(event.getTextChannel());
        // }
    }

}