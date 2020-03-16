package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Skip extends Command {

    protected Skip() {
        super("Skip", "skip");
        setDescription("Skips the currently playing track.");
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
        guildMusicManager.scheduler.nextTrack();
    }
}