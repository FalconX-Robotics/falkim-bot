package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.core.BotConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * LoadHandler
 */
public class LoadHandler implements AudioLoadResultHandler {

    private TrackScheduler scheduler;
    private MessageChannel messageChannel;

    public LoadHandler(TrackScheduler scheduler, MessageChannel messageChannel) {
        this.scheduler = scheduler;
        this.messageChannel = messageChannel;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        scheduler.queue(track);
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("Queued")
            .addField("Title", track.getInfo().title, false)
            .addField("Duration", Music.getInstance().inReadable(track.getDuration()), false)
            .setColor(BotConfiguration.getSuccessColor());
        messageChannel.sendMessage(builder.build()).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playlist.getTracks().stream().forEach(scheduler::queue);
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("Queued")
            .addField("Name", playlist.getName(), false)
            .addField("Length", String.valueOf(playlist.getTracks().size()), false)
            .setColor(BotConfiguration.getSuccessColor());
        messageChannel.sendMessage(builder.build()).queue();
    }

    @Override
    public void noMatches() {
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("No Matches")
            .setDescription("The song was not queued")
            .setColor(BotConfiguration.getErrorColor());
        messageChannel.sendMessage(builder.build()).queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("Load Failed")
            .setDescription("The song was not queued")
            .setColor(BotConfiguration.getErrorColor());
        messageChannel.sendMessage(builder.build()).queue();
    }
}