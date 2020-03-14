package com.falconxrobotics.discordbot.commands;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Pray
 */
public class Poll extends Command {

    private static String[] emojis = new String[] {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"};

    public Poll() {
        super("Poll", "poll");
        setDescription("Makes a poll.");
        setSyntax("[question] [...options]");
        addExamples("What's better?", "Dogs", "Cats");
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
        EmbedBuilder builder = new EmbedBuilder().setTitle(arguments[0]);

        if (arguments.length > 11) {
            event.getChannel().sendMessage(getEmbedInvalidParameterError("Cannot exceed more than 10 options.").build()).queue();
            return;
        }

        for (int i = 1; i < arguments.length; i++) {
            builder.appendDescription(emojis[i-1] + "  " + arguments[i] + "\n");
            // builder.addField("", emojis[i-1] + ": " + arguments[i], false);
        }

        event.getChannel().sendMessage(builder.build()).queue((m) -> {
            for (int i = 1; i < arguments.length; i++) {
                m.addReaction(emojis[i-1]).queue();
            }
        });
    }
}