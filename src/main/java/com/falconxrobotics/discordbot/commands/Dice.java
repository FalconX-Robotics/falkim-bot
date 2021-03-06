package com.falconxrobotics.discordbot.commands;

import java.util.Random;

import com.github.raybipse.components.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Dice extends Command {

    public Dice() {
        super("Dice", "dice");
        setDescription("Supplies a random value from [1, roll faces]. By default, it rolls from [1, 6].");
        addExamples("", "2");
        setSyntax("[optional: roll amount (whole number); max: " + maxRollAmonut + "] [optional: dice faces (natural number); max: " + maxRollFaces+"]");
    }

    private Random rand = new Random(); 
    private int maxRollAmonut = 5;
    private int maxRollFaces = Integer.MAX_VALUE;

    private String[] diceAscii = new String[] {
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n" +
        "|                   |\n" +
        "|                   |\n" +         
        "|         o        |\n" +
        "|                   |\n" +
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n",
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n" +
        "|                   |\n" +
        "|      o           |\n" + 
        "|                   |\n" + 
        "|           o      |\n" + 
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n",
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n" +
        "|                   |\n" +
        "|         o        |\n" + 
        "|                   |\n" + 
        "|     o      o    |\n" + 
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n",
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n" +
        "|                    |\n" +
        "|    o        o    |\n" + 
        "|                    |\n" + 
        "|    o        o    |\n" + 
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n",
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n" +
        "|                    |\n" +
        "|     o      o     |\n" + 
        "|         o         |\n" + 
        "|     o      o     |\n" + 
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n",
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n" +
        "|                    |\n" +
        "|     o      o     |\n" + 
        "|     o      o     |\n" + 
        "|     o      o     |\n" + 
        "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\n"
    };

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        String[] arguments = splitUserInput(messageContent);
        int rollAmount = 1;
        int diceFaces = 6;

        if (arguments.length == 1 && arguments[0].equals("help")) {
            event.getChannel().sendMessage(getEmbedInfo().build()).queue();
            return;
        } 

        try {
            if (arguments.length >= 1) rollAmount = Integer.valueOf(arguments[0]);
        } catch (NumberFormatException nfe) {
            event.getChannel().sendMessage(getEmbedInvalidParameterTypes().build()).queue();
            return;
        }
        try {
            if (arguments.length >= 2) diceFaces = Integer.valueOf(arguments[1]);
        } catch (NumberFormatException nfe) {
            event.getChannel().sendMessage(getEmbedInvalidParameterTypes().build()).queue();
            return;
        }

        if (rollAmount > maxRollAmonut) {
            event.getChannel().sendMessage(getEmbedInvalidParameterError("Roll amount cannot be above " + maxRollAmonut + ".").build()).queue();
            return;
        } else if (diceFaces > maxRollFaces) {
            event.getChannel().sendMessage(getEmbedInvalidParameterError("Dice faces cannot be above " + maxRollFaces + ".").build()).queue();
            return;
        }
        if (rollAmount < 0) {
            event.getChannel().sendMessage(getEmbedInvalidParameterError("Roll amount cannot be negative").build()).queue();
            return;
        } else if (diceFaces < 2) {
            event.getChannel().sendMessage(getEmbedInvalidParameterError("Dice face must be at least 2").build()).queue();
            return;
        }

        rand.setSeed(System.currentTimeMillis());

        if (rollAmount == 1 && diceFaces <= 6) {
            event.getChannel().sendMessage(diceAscii[rand.nextInt(diceFaces)]).queue();
            return;
        }

        for (int i = 0; i < rollAmount; i++) {
            event.getChannel().sendMessage("Dice. You got " + (rand.nextInt(diceFaces) + 1)).queue();
        }
    }

}