package com.falconxrobotics.discordbot.commands.music;

import java.awt.Color;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Now extends Command {

    protected Now() {
        super("Now", "now");
        setDescription("Gives information about the currently playing track.");
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
        AudioTrack track = guildMusicManager.player.getPlayingTrack();
        EmbedBuilder builder = new EmbedBuilder();
        if (track == null) {
            builder.setTitle("No Tracks Playing").setColor(Color.ORANGE);
        } else {
            builder = Music.getInstance().getEmbedTrackInfo(track.getInfo())
                .setColor(BotConfiguration.getSuccessColor())
                .setFooter(guildMusicManager.scheduler.queue.size() + " more queued audio tracks.");
        }

        event.getChannel().sendMessage(builder.build()).queue();
    }
}