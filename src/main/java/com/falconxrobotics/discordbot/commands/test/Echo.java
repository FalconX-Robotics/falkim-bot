package com.falconxrobotics.discordbot.commands.test;

import com.github.raybipse.components.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command that repeats the arguments of the input.
 * 
 * @author RayBipse
 */
public class Echo extends Command {

    protected Echo() {
        super("Echo", "echo");
        setDescription("Repeats the parameters the user gives.");
        addExamples("\"Hello World!\"");
        setSyntax("[...things to be repeated]*");
        setParent(Test::getInstance);
    }

    @Override
    public EmbedBuilder getEmbedInfo() {
        EmbedBuilder builder = super.getEmbedInfo();
        builder.addField("Tip", "Add quotation marks around the message to echo a sentence without sending multiple messages.", false);
        return builder;
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
        } else if (arguments.length == 1) {
            event.getChannel().sendMessage(arguments[0]).queue();
        } else {
            for (String arg : arguments) {
                event.getChannel().sendMessage(arg).queue();
            }
        }
    }
}