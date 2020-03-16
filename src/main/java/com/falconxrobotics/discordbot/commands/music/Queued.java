package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Queued extends Command {

    protected Queued() {
        super("Queued", "queued");
        setDescription("Gives information about the queued tracks.");
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
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(BotConfiguration.getPromptColor())
            .setTitle("Queued Tracks")
            .setFooter("This only shows 10 of the queued tracks.");
            
        int[] i = new int[] {1};
        guildMusicManager.scheduler.queue.stream().limit(10).forEach(v -> {
            builder.addField(i[0]++ + ". " + v.getInfo().title, "Duration: " + Music.getInstance().inReadable(v.getDuration()), false);
        });

        if (guildMusicManager.scheduler.queue.isEmpty()) {
            builder.setDescription("No tracks queued,");
        }

        event.getChannel().sendMessage(builder.build()).queue();
    }
}