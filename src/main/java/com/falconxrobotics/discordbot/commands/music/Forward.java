package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Forward extends Command {

    protected Forward() {
        super("Forward", "forward");
        setDescription("Forwards the currently playing track by the specified number of seconds.");
        addExamples("", "23");
        setSyntax("[optional: seconds; default: 30]");
        setParent(Music::getInstance);
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
        double seconds = 30;
        if (arguments.length > 0) {
            try {
                seconds = Double.valueOf(arguments[0]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(getEmbedInvalidParameterTypes().build()).queue();
                return;
            }
        }
        AudioPlayer player = Music.getInstance().getGuildMusicManager(event.getGuild()).player;
        player.getPlayingTrack().setPosition((long) (player.getPlayingTrack().getPosition()+(1000*seconds)));
        event.getChannel().sendMessage(
            new EmbedBuilder()
                .setTitle("Forwarded " + (long) seconds + " Seconds")
                .addField("Position", Music.getInstance().inReadable(player.getPlayingTrack().getPosition()), false)
                .build()
        ).queue();
    }
}