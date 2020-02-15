package com.falconxrobotics.discordbot.commands;

import com.falconxrobotics.discordbot.Bot;
import com.github.raybipse.components.Command;
import com.github.raybipse.components.CommandGroup;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

/**
 * A help command that tells general information about the bot.
 * 
 * @author RayBipse
 */
public class Help extends Command {

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    public String getPrefix() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Returns details and general information about the bot.";
    }

    @Override
    public String[] getExamples() {
        return new String[] { "" };
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public CommandGroup getParent() {
        return null;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        CommandGroup[] allCommandGroups = Bot.getAllCommandGroups();
        String[] allCommandGroupPrefixes = new String[allCommandGroups.length];
        for (int i = 0; i < allCommandGroups.length; i++) {
            allCommandGroupPrefixes[i] = allCommandGroups[i].getPrefix();
        }

        Command[] allStandaloneCommands = Bot.getAllStandaloneCommands();
        String[] allStandaloneCommandPrefixes = new String[allStandaloneCommands.length];
        for (int i = 0; i < allStandaloneCommands.length; i++) {
            allStandaloneCommandPrefixes[i] = allStandaloneCommands[i].getPrefix();
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Help")
                .setDescription("Peter Kim Jr., or Falkim Bot, is a Discord Bot created by FalconX Robotics.")
                .setColor(BotConfiguration.getPromptColor())
                .addField("Structure", 
                        "Commands can be stand alone or can be grouped into a \"``CommandGroup``\". "
                        + "Every command in a command group have their own \"``help``\" command embeded in them. "
                        + "To see the description and usage of those commands, type ``"
                        + BotConfiguration.getBotPrefix() + "[command group prefix] help [specific command]``. "
                        + "To see the description of a command group, type ``" + BotConfiguration.getBotPrefix()
                        + "[command group prefix] help``.", false)
                .addField("Arguments", 
                        "Command arguments are separated by spaces. E.g., \"``/command argument1 argument2``\". "
                        + "If an argument requires spaces in it, wrap it around quotation marks. E.g., "
                        + "\"``/command \"this is one argument\" argument 2``\". "
                        + "Add a backslash if an argument requires quotations in it. You must type a double backslash because Discord "
                        + "automatically deletes single backslashs when the message is sent.", false)
                .addField("Bot prefix", MarkdownUtil.monospace(BotConfiguration.getBotPrefix()), false);
        if (allCommandGroupPrefixes.length > 0) {
            System.out.println(String.join("`` ", allCommandGroupPrefixes));
            builder.addField("All command group prefixes", "``"+String.join("`` ``", allCommandGroupPrefixes)+"``", false);
        } else {
            builder.addField("All command group prefixes", "No visible command groups.", false);
        }
        if (allStandaloneCommandPrefixes.length > 0) {
            builder.addField("All standalone command prefixes", "``"+String.join("`` ", allStandaloneCommandPrefixes)+"``", false);
        } else {
            builder.addField("All standalone command prefixes", "No visible commands.", false);
        }
        builder.addField("Source code", "[GitHub](https://github.com/RayBipse/falkim-bot)", false);
        builder.setFooter("Note: Some commands/command groups are hidden.");
        
        event.getChannel().sendMessage(builder.build()).queue();
    }

}