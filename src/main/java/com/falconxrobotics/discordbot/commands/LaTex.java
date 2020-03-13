package com.falconxrobotics.discordbot.commands;

import java.io.IOException;
import java.net.URL;

import com.github.raybipse.components.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * LaTex http://latex.codecogs.com/png.latex?123
 */

/**
 * A command that supplies a random value.
 * 
 * @author RayBipse
 */
public class LaTex extends Command {

    public LaTex() {
        super("LaTex", "latex");
        setDescription("Generates a LaTex image.");
        addExamples("\\frac{x}{x-1}", "\\int_{x}^{y}z");
        setSyntax("[latex string]");
    } 

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        String input = trimInputBeginning(messageContent);

        if (input.isEmpty()) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
            return;
        }

        if (input.equals("help")) {
            event.getChannel().sendMessage(getEmbedInfo().build()).queue();
            return;
        }

        try {
            event.getChannel().sendFile(
                    new URL("http://latex.codecogs.com/png.latex?\\bg_white&space;\\large&space;" + input).openStream(),
                    "latex.png").queue();
        } catch (IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(getEmbedSimpleError("IO Exception",
                    "Unexpected IO Exception. Perhaps your LaTex expression is invalid.").build()).queue();
        }
    }

}