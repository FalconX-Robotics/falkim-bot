package com.falconxrobotics.discordbot.commands.corona;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.Invocable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class All extends Command implements Invocable<Void, EmbedBuilder> {

    private JSONParser parser = new JSONParser();
    private HttpClient client = HttpClient.newHttpClient();
    private URI fetchURI = URI.create("https://corona.lmao.ninja/all");

    protected All() {
        super("All", "all");
        setDescription("Gives status of COVID-19 in the world.");
        setSyntax("");
        addExamples("");
        setParent(Coronavirus::getInstance);
    }

    @Override
    public EmbedBuilder invoke(Void placeholder) {
        HttpRequest request = HttpRequest.newBuilder().uri(fetchURI).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = (JSONObject) parser.parse(response.body());
            EmbedBuilder builder = new EmbedBuilder()
                .setTitle("COVID-19 in the Globe");

            builder.addField("Cases", json.get("cases").toString(), false);
            builder.addField("Deaths", json.get("deaths").toString(), false);
            builder.addField("Recovered", json.get("recovered").toString(), false);
            builder.setFooter("Source: https://www.worldometers.info/coronavirus/");
            return builder;
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return getEmbedSimpleError("IOException | InterruptedException Exception", "Unexpected response");
        } catch (ParseException e) {
            e.printStackTrace();
            return getEmbedSimpleError("Parse Exception", "Unexpected response");
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

        EmbedBuilder builder = invoke(null);
        if (builder != null) {
            event.getChannel().sendMessage(builder.build()).queue();
        }
    }
}