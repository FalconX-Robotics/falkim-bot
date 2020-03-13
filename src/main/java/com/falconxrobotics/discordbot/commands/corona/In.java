package com.falconxrobotics.discordbot.commands.corona;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.Invocable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class In extends Command implements Invocable<String, EmbedBuilder> {

    private JSONParser parser = new JSONParser();
    private HttpClient client = HttpClient.newHttpClient();
    private URI fetchURI = URI.create("https://corona.lmao.ninja/countries");

    public In() {
        super("In", "in");
        setDescription("Gives status of COVID-19 in a specified country.");
        setSyntax("[country]");
        setParent(() -> Coronavirus.getInstance());
        addExamples("USA", "China");
    }

    /**
     * Credit: https://stackoverflow.com/a/2560017/11039690
     */
    static String splitCamelCase(String s) {
        s = s.replaceAll(
           String.format("%s|%s|%s",
              "(?<=[A-Z])(?=[A-Z][a-z])",
              "(?<=[^A-Z])(?=[A-Z])",
              "(?<=[A-Za-z])(?=[^A-Za-z])"
           ),
           " "
        );
        return s.substring(0, 1).toUpperCase() + s.substring(1);
     }

    @Override
    @SuppressWarnings("unchecked")
    public EmbedBuilder invoke(String country) {
        HttpRequest request = HttpRequest.newBuilder().uri(fetchURI).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray json = (JSONArray) parser.parse(response.body());

            Optional<JSONObject> optional = json.stream().filter((e) -> ((JSONObject) e).get("country").equals(country))
                    .findFirst();
            
            if (!optional.isPresent()) {
                return getEmbedInvalidParameterError("Invalid Country Name");
            } else {
                JSONObject info = optional.get();
                EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("COVID-19 cases in " + (String) info.get("country"))
                    .setFooter("Source: https://www.worldometers.info/coronavirus/");
                info.forEach((k, v) -> {
                    if (!k.equals("country")) {
                        builder.addField(splitCamelCase(k.toString()), v.toString(), false);
                    }
                });
                return builder;
            }
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

        if (messageContent == null || messageContent.isBlank()) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            EmbedBuilder builder = invoke(messageContent);
            if (builder != null) {
                event.getChannel().sendMessage(builder.build()).queue();
            }
        }
    }
}