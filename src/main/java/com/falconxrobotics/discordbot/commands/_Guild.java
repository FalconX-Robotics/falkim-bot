package com.falconxrobotics.discordbot.commands;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.Invocable;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class _Guild extends Command implements Invocable<Guild, EmbedBuilder> {

    public _Guild() {
        super("Guild", "guild");
        setDescription("Returns details and general information about the guild.");
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

        event.getChannel().sendMessage(invoke(event.getGuild()).build()).queue();
    }

    @Override
    public EmbedBuilder invoke(Guild guild) {
        return new EmbedBuilder()
            .setTitle("Guild: " + guild.getName())
            .setThumbnail(guild.getIconUrl())
            .addField("Creation Date", guild.getTimeCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), true)
            .addField("Roles (" + guild.getRoles().size() + ")", Arrays.toString(guild.getRoles().stream().map(Role::getName).toArray(String[]::new)), false)
            .addField("ID", guild.getId(), true)
            .addField("Owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
            .addField("Member Count", String.valueOf(guild.getMembers().size()), true)
            .addField("Bot Count", String.valueOf(guild.getMembers().stream().filter(m -> m.getUser().isBot()).count()), true)
            .addField("Emotes (" + guild.getEmotes().size() + ")", Arrays.toString(guild.getEmotes().stream().map(e -> e.getAsMention()).toArray()), true)
            .setColor(BotConfiguration.getPromptColor());
    }
    
}