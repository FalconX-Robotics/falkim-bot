package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.core.BotConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 * 
 * @author sedmelluq
 * @author RayBipse
 */
public class TrackScheduler extends AudioEventAdapter {
    protected final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    protected final GuildMusicManager guildMusicManager;

    public AudioTrack currentTrack;
    private boolean isLooped;
    // private boolean isPausedDB = false;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, GuildMusicManager guildMusicManager) {
        this.player = player;
        this.guildMusicManager = guildMusicManager;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only
        // if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the
        // player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        } else {
            currentTrack = track;
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    public void setLooped(boolean isLooped) {
        this.isLooped = isLooped;
    }

    public boolean getLooped() {
        return isLooped;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not.
        // In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the
        // player.

        if (!isLooped && currentTrack != null) {
            currentTrack = queue.poll();
        } else {
            currentTrack = currentTrack.makeClone();
        }
        player.startTrack(currentTrack, false);

        if (currentTrack == null) {
            Music.getInstance().stop.invoke(guildMusicManager.guild);
            EmbedBuilder builder = new EmbedBuilder().setTitle("Queue ended")
                    .setDescription("All songs have finished playing.").setColor(Color.YELLOW);
            guildMusicManager.channel.sendMessage(builder.build()).queue();
        }

        EmbedBuilder builder = Music.getInstance().getEmbedTrackInfo(currentTrack.getInfo()).setColor(Color.ORANGE);

        String appen = "";
        if (guildMusicManager.scheduler.isLooped) {
            appen = "Current track on loop. ";
        }
        if (queue.size() > 0) {
            builder.setFooter(appen + queue.size() + " more queued audio tracks.");
        } else if (queue.size() == 0) {
            builder.setFooter(appen + "No more queued audio tracks.");
        }
        guildMusicManager.channel.sendMessage(builder.build()).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or
        // LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    // public void pause() {
    //     player.setPaused(true);
    // }

    // public void resume() {
    //     player.setPaused(false);
    // }

    public void togglePaused() {
        player.setPaused(!player.isPaused());
    }

    /**
     * @param player Audio player
     */
    public void onPlayerPause(AudioPlayer player) {
        // if (isPausedDB == false) isPausedDB = true;
        // else return;
        guildMusicManager.channel.sendMessage(
            new EmbedBuilder()
                .setTitle("Paused")
                .setColor(Color.YELLOW)
                .build()
        ).queue();
    }

    /**
     * @param player Audio player
     */
    public void onPlayerResume(AudioPlayer player) {
        // if (isPausedDB == true) isPausedDB = false;
        // else return;
        guildMusicManager.channel.sendMessage(
            new EmbedBuilder()
                .setTitle("Resumed")
                .setColor(BotConfiguration.getSuccessColor())
                .build()
        ).queue();
    }
}