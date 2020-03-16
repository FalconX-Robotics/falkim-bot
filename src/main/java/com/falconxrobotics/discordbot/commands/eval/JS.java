package com.falconxrobotics.discordbot.commands.eval;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.Invocable;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class JS extends Command implements Invocable<String, EmbedBuilder> {

    private ScriptEngineManager mgr = new ScriptEngineManager();
    private ScriptEngine engine = mgr.getEngineByName("JavaScript");

    protected JS() {
        super("JavaScript", "js");
        setDescription("Evaulates JavaScript code on the Nashorn engine.");
        setSyntax("[code]");
        setParent(Evaluate::getInstance);
        addExamples("1 + 1", "[1,2,3].reduce(function(a, b) { return a+b })");
    }

    @Override
    public EmbedBuilder invoke(String code) {
        try {
            Object returned = engine.eval(code);
            return new EmbedBuilder()
                .setTitle("Results")
                .setDescription(returned == null ? "No result" : returned.toString());
        } catch (ScriptException e) {
            e.printStackTrace();
            return getEmbedSimpleError("Script Exception", e.getMessage()).setFooter("This JavaScript is ran on the Nashorn engine. Many ES6 features are not yet implemented.");
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
        event.getChannel().sendMessage(invoke(messageContent).build()).queue();
    }
}