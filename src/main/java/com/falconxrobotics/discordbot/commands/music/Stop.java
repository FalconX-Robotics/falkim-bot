package com.falconxrobotics.discordbot.commands.music;

import java.awt.Color;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.Invocable;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Stop extends Command implements Invocable<Guild, Void> {

    protected Stop() {
        super("Stop", "stop");
        setDescription("Stops playing.");
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

        invoke(event.getGuild());
    }

    @Override
    public Void invoke(Guild guild) {
        Music.getInstance().getGuildMusicManager(guild).scheduler.clearQueue();
        guild.getAudioManager().closeAudioConnection();

        Music.getInstance().getGuildMusicManager(guild).channel.sendMessage(
            new EmbedBuilder()
                .setTitle("Stopped Playing")
                .setColor(Color.YELLOW)
                .build()
        ).queue();

        return null;
    }
}