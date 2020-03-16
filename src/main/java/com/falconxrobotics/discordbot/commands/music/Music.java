package com.falconxrobotics.discordbot.commands.music;

import java.util.HashMap;
import java.util.Map;

import com.github.raybipse.components.CommandGroup;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.Guild;


/**
 * A command group that contains commands that is used to play music.
 * 
 * @author RayBipse
 */
public class Music extends CommandGroup {
    private static Music instance;
    private Play play = new Play();
    protected Stop stop = new Stop();
    private Skip skip = new Skip();

    public final AudioPlayerManager playerManager;
    protected final Map<Long, GuildMusicManager> musicManagers = new HashMap<Long, GuildMusicManager>();

    protected Music() {
        super("Music", "m");
        setDescription("Commands that is used to play music.");
        addChildren(play, stop, skip, new Help());

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    /**
     * @return the sole instance of the class
     */
    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        GuildMusicManager musicManager = musicManagers.get(guild.getIdLong());

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guild.getIdLong(), musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public String inReadable(long millis) {
        long seconds = millis/1000;
        long numberOfHours = (seconds % 86400 ) / 3600;
        long numberOfMinutes = ((seconds % 86400 ) % 3600 ) / 60; 
        long numberOfSeconds = ((seconds % 86400 ) % 3600 ) % 60;
        String result = "";
        if (numberOfHours != 0) {
            result+=numberOfHours+":";
        }
        if (numberOfMinutes != 0) {
            result+=(String.valueOf(numberOfMinutes).length() == 1 ? "0" : "") + numberOfMinutes+":";
        } else {
            result+="00:";
        }

        result+=(String.valueOf(numberOfSeconds).length() == 1 ? "0" : "") +  numberOfSeconds;
        return result;
    }
}