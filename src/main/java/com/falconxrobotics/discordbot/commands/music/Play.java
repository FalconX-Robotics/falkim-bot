package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * @author RayBipse
 */
public class Play extends Command { 

    // protected TrackScheduler trackScheduler;
    // protected DefaultAudioPlayerManager playerManager;
    // protected AudioPlayer player;
    // protected AudioPlayerSendHandler audioPlayerSendHandler;
    // protected LoadHandler loadHandler;

    protected Play() {
        super("Play", "play");
        setDescription("Plays a track.");
        addExamples("https://youtu.be/Gz1ldpRfg74", "dQw4w9WgXcQ");
        setSyntax("[link] OR [sometimes: video id]");
        setParent(Music::getInstance);

        // player = playerManager.createPlayer();
        // trackScheduler = new TrackScheduler(player);
        // audioPlayerSendHandler = new AudioPlayerSendHandler(player);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        String[] arguments = splitUserInput(messageContent);

        if (arguments.length == 0) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            AudioManager manager = event.getGuild().getAudioManager();
            VoiceChannel channel = getUserVoiceChannel(manager, event.getMember());

            if (channel == null) {
                event.getChannel().sendMessage(getEmbedSimpleError("Voice Channel Not Found", "Join a voice channel.").build()).queue();
                return;
            }

            GuildMusicManager guildMusicManager = Music.getInstance().getGuildMusicManager(event.getGuild());
            guildMusicManager.setChannel(event.getChannel());

            if (arguments[0].equalsIgnoreCase("rick")) {
                arguments[0] = "dQw4w9WgXcQ";
            }

            Music.getInstance().playerManager.loadItemOrdered(guildMusicManager.player, arguments[0], new LoadHandler(guildMusicManager.scheduler, event.getChannel()));
            manager.setSendingHandler(guildMusicManager.getSendHandler());
            manager.openAudioConnection(channel);
        }
    }

    /**
     * @return null if not successful
     */
    private VoiceChannel getUserVoiceChannel(AudioManager audioManager, Member user) {
        for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(user)) {
                return voiceChannel;
            }
        }
        return null;
    }
    
}