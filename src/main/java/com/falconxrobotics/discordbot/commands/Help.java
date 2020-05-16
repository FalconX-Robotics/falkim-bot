package com.falconxrobotics.discordbot.commands;

import java.util.Optional;

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

    private static Help instance;

    private Help() {
        super("Help", "help");
        setDescription("Returns details and general information about the bot.");
        setSyntax("[optional: command] OR [command group] [command]");
        addExamples("");
    }

    public static Help getInstance() {
        if (instance == null) {
            instance = new Help();
        }
        return instance;
    }

    private void onNoArgument(MessageReceivedEvent event) {
        String[] allCommandGroupPrefixes = BotConfiguration.getAllCommandGroups().stream().map(CommandGroup::getPrefix)
                .toArray(String[]::new);
        String[] allStandaloneCommandPrefixes = BotConfiguration.getAllStandaloneCommands().stream()
                .map(Command::getPrefix).toArray(String[]::new);

        EmbedBuilder builder = new EmbedBuilder().setTitle("Help")
                .setDescription("Peter Kim Jr., or Falkim Bot, is a Discord Bot created by FalconX Robotics.")
                .setColor(BotConfiguration.getPromptColor())
                .addField("Structure", "Commands can be stand alone or can be grouped into a \"``CommandGroup``\". "
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
                                + "automatically deletes single backslashs when the message is sent.",
                        false)
                .addField("Bot prefix", MarkdownUtil.monospace(BotConfiguration.getBotPrefix()), false);
        if (allCommandGroupPrefixes.length > 0) {
            builder.addField("All command group prefixes", "``" + String.join("``, ``", allCommandGroupPrefixes) + "``",
                    false);
        } else {
            builder.addField("All command group prefixes", "No visible command groups.", false);
        }
        if (allStandaloneCommandPrefixes.length > 0) {
            builder.addField("All standalone command prefixes",
                    "``" + String.join("``, ``", allStandaloneCommandPrefixes) + "``", false);
        } else {
            builder.addField("All standalone command prefixes", "No visible commands.", false);
        }
        builder.addField("Source code", "[GitHub](https://github.com/RayBipse/falkim-bot)", false);
        builder.setFooter("Note: Some commands/command groups are hidden.");

        event.getChannel().sendMessage(builder.build()).queue();
    }

    private void onCommandHelp(MessageReceivedEvent event, String prefix) {
        Optional<Command> optional = BotConfiguration.getAllStandaloneCommands().stream()
                .filter(e -> e.getPrefix().equals(prefix)).findFirst();
        if (optional.isPresent()) {
            event.getChannel().sendMessage(optional.get().getEmbedInfo().build()).queue();
        } else {
            event.getChannel()
                    .sendMessage(getEmbedInvalidParameterError("Command Not Found")
                            .addField("Note",
                                    "If you want see the help command for a Command Group, use "
                                            + BotConfiguration.getBotPrefix() + "[command group prefix] help",
                                    false)
                            .build())
                    .queue();
        }
    }

    private void onCommandGroupHelp(MessageReceivedEvent event, String groupPrefix, String commandPrefix) {
        Optional<CommandGroup> commandGroupOptional = BotConfiguration.getAllCommandGroups().stream()
                .filter(e -> e.getPrefix().equals(groupPrefix)).findFirst();
        if (commandGroupOptional.isPresent() && commandGroupOptional.get().getChildren() != null) {
            Optional<Command> commandOptional = commandGroupOptional.get().getChildren().stream()
                .filter(e -> e.getPrefix().equals(commandPrefix)).findFirst();
            if (commandOptional.isPresent()) {
                event.getChannel().sendMessage(commandOptional.get().getEmbedInfo().build()).queue();
            } else {
                event.getChannel().sendMessage(getEmbedInvalidParameterError("Command Not Found Within Command Group").build()).queue();
            }
        } else {
            event.getChannel().sendMessage(getEmbedInvalidParameterError("Command Group Not Found").build()).queue();
        }
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
            onNoArgument(event);
        } else if (arguments.length == 1) {
            onCommandHelp(event, arguments[0]);
        } else {
            onCommandGroupHelp(event, arguments[0], arguments[1]);
        }
    }

}