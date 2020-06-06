package com.falconxrobotics.discordbot.commands.reddit;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Top extends Command {

    private JSONParser parser = new JSONParser();
    private HttpClient client = HttpClient.newHttpClient();
    private Timer timer;
    private String defaultTimerSubreddit = "BigChungus";
    private int targetTimerHour = 4;
    private int targetTimerMinute = 20;

    protected Top() {
        super("Top", "top");
        setDescription("Gets the top post from a subreddit.");
        addExamples("okbuddyretard");
        setSyntax("[subreddit name]");
        setParent(Reddit::getInstance);

        setUpTimer();
    }

    private void setUpTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Date.from(Instant.now()));
                cal.setTimeZone(TimeZone.getTimeZone("PST"));
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                if ((hour == targetTimerHour || hour == targetTimerHour + 12) && minutes == targetTimerMinute) {
                    TextChannel channel = BotConfiguration.getJDA().getTextChannelById(533820195949510666L);
                    channel.sendMessage("It's " + targetTimerHour + ":" + targetTimerMinute + ", time for a " + defaultTimerSubreddit + " meme.")
                        .queue(m -> sendRequest(channel, defaultTimerSubreddit));
                }
            }
        }, 0, 60 * 1000);
    }

    @SuppressWarnings("unchecked")
    private void sendRequest(TextChannel channel, String subreddit) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.reddit.com/r/" + subreddit + "/top/.json?limit=1")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = (JSONObject) parser.parse(response.body());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) ((JSONArray) ((Map<String, Object>) json
                    .get("data")).get("children")).get(0)).get("data");

            if ((boolean) data.get("over_18") || (boolean) data.get("quarantine")) {
                channel.sendMessage(
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

            channel.sendMessage(builder.build()).queue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            channel
                    .sendMessage(
                            getEmbedSimpleError("Unexpected Exception", "Error message: " + e.getMessage()).build())
                    .queue();
        } catch (ParseException pe) {
            pe.printStackTrace();
            channel.sendMessage(getEmbedSimpleError("Parse Exception",
                    "Unexpected response from reddit. Perhaps the subreddit, \"" + subreddit + "\", does not exist.")
                            .build())
                    .queue();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            channel
                    .sendMessage(getEmbedSimpleError("Null Pointer Exception",
                            "Unexpected null pointer exception. Perhaps the subreddit, \"" + subreddit
                                    + "\", is quarantined.").build())
                    .queue();
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
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            sendRequest(event.getTextChannel(), arguments[0]);
        }
    }
}