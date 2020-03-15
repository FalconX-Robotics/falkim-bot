package com.falconxrobotics.discordbot.commands;

import java.util.regex.Pattern;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Javadoc extends Command {

    public Javadoc() {
        super("Javadoc", "javadoc");
        setDescription("Gives javadoc link of JDK 11.");
        addExamples("java/util/Consumer", "java.util.Consumer", "");
        setSyntax("[class path]");
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

        if (arguments.length == 0 || arguments[0].isEmpty()) {
            event.getChannel().sendMessage("https://docs.oracle.com/en/java/javase/11/docs/api/").queue();
        } else {
            event.getChannel().sendMessage("https://docs.oracle.com/en/java/javase/11/docs/api/java.base/" + arguments[0].replaceAll(Pattern.quote("."), "/") + ".html").queue();

        }

    }

}