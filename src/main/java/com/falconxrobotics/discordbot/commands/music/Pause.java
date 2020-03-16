package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Pause extends Command {

    protected Pause() {
        super("Pause", "pause");
        setDescription("Toggles pause on the currently playing music.");
        addExamples("");
        setSyntax("");
        setParent(Music::getInstance);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        GuildMusicManager guildMusicManager = Music.getInstance().getGuildMusicManager(event.getGuild());
        guildMusicManager.scheduler.togglePaused();
    }
}