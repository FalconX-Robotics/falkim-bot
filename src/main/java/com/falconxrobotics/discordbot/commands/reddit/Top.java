package com.falconxrobotics.discordbot.commands.reddit;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.github.raybipse.components.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command the top post from a subreddit.
 * 
 * @author RayBipse
 */
public class Top extends Command {

    private JSONParser parser = new JSONParser();
    private HttpClient client = HttpClient.newHttpClient();

    protected Top() {
        super("Top", "top");
        setDescription("Gets the top post from a subreddit.");
        addExamples("okbuddyretard");
        setSyntax("[subreddit name]");
        setParent(Reddit.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
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
        } else {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.reddit.com/r/" + arguments[0] + "/top/.json?limit=1")).build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject json = (JSONObject) parser.parse(response.body());
                Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) ((JSONArray) ((Map<String, Object>) json
                        .get("data")).get("children")).get(0)).get("data");

                if ((boolean) data.get("over_18") || (boolean) data.get("quarantine")) {
                    event.getChannel().sendMessage(
                            getEmbedSimpleError("Inappropriate Post", "The returned post is marked as NSFW or quarantined.")
                                    .build())
                            .queue();
                    return;
                }

                String url = (String) data.get("permalink");
                String title = (String) data.get("title");
                String thumbnail = (String) data.get("url");

                EmbedBuilder builder = new EmbedBuilder().setTitle(title, "https://www.reddit.com" + url);

                if (thumbnail == null || thumbnail.isBlank()) {
                    builder.setDescription("No preview for this post is available.");
                } else {
                    builder.setImage(thumbnail);
                }

                event.getChannel().sendMessage(builder.build()).queue();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                event.getChannel()
                        .sendMessage(
                                getEmbedSimpleError("Unexpected Exception", "Error message: " + e.getMessage()).build())
                        .queue();
            } catch (ParseException pe) {
                pe.printStackTrace();
                event.getChannel()
                        .sendMessage(getEmbedSimpleError("Parse Exception",
                                "Unexpected response from reddit. Perhaps the subreddit, \"" + arguments[0]
                                        + "\", does not exist.").build())
                        .queue();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                event.getChannel()
                        .sendMessage(getEmbedSimpleError("Null Pointer Exception",
                                "Unexpected null pointer exception. Perhaps the subreddit, \"" + arguments[0]
                                        + "\", is quarantined.").build())
                        .queue();
            }
        }
    }
}