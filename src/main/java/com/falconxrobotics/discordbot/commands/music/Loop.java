package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Loop extends Command {

    protected Loop() {
        super("Loop", "loop");
        setDescription("Loops the current track.");
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
        boolean loopTo = !guildMusicManager.scheduler.getLooped();
        guildMusicManager.scheduler.setLooped(loopTo);

        if (guildMusicManager.scheduler.currentTrack == null) {
            event.getChannel().sendMessage(
                    getEmbedSimpleError("No Track Playing", "You can only loop a track if a track is playing.").build())
                    .queue();
        } else {
            event.getChannel()
                    .sendMessage(new EmbedBuilder()
                            .setTitle("Set Loop to: " + loopTo).setDescription("\""
                                    + guildMusicManager.scheduler.currentTrack.getInfo().title + "\" will be on loop.")
                            .setColor(loopTo ? BotConfiguration.getSuccessColor() : BotConfiguration.getErrorColor())
                            .build())
                    .queue();
        }
    }
}