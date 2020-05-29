package com.falconxrobotics.discordbot.commands.spacex;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command the top post from a subreddit.
 * 
 * @author RayBipse
 */
public class Next extends Command {

    private JSONParser parser = new JSONParser();
    private HttpClient client = HttpClient.newHttpClient();
    private Timer timer;

    protected Next() {
        super("Next", "next");
        setDescription("Gets upcoming launch.");
        addExamples("");
        setSyntax("");
        setParent(SpaceX::getInstance);

        setUpTimer();
    }

    private void setUpTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {                
                TextChannel channel = BotConfiguration.getJDA().getTextChannelById(533820195949510666L);

                Date now = Date.from(Instant.now());
                Date launchUTC = getLaunchUTC();
                
                long timeDif = launchUTC.getTime() - now.getTime();
                long minutes = (timeDif / 1000)  / 60;
                if (minutes == 30) {
                    channel.sendMessage("SpaceX: ``30`` minutes before launch :rocket:").queue();;
                    sendRequest(channel);
                }
            }
        }, 0, 60 * 1000);
    }

    private Date getLaunchUTC() {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.spacexdata.com/v3/launches/next"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = (JSONObject) parser.parse(response.body());

            return new Date( ((Long) json.get("launch_date_unix")).longValue()  * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private EmbedBuilder getBuilder() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spacexdata.com/v3/launches/next")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = (JSONObject) parser.parse(response.body());
        EmbedBuilder builder = new EmbedBuilder();

        String missionName = (String) json.get("mission_name");
        Date date = new Date( ((Long) json.get("launch_date_unix")).longValue()  * 1000L); 
        // the format of your date
        DateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.ENGLISH);  //"yyyy-MM-dd HH:mm:ss"
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-7"));

        Map<String, String> links = (Map<String, String>) json.get("links");
        String youtubeLink = links.get("youtube_id");
        String missionPatch = links.get("mission_patch");

        String rocketName = ((Map<String, String>) json.get("rocket")).get("rocket_name");
        String launchSite = ((Map<String, String>) json.get("launch_site")).get("site_name");

        builder.setTitle("Mission: \""+ missionName + "\"");
        builder.addField("Time (PST)", sdf.format(date), false);
        if (youtubeLink != null) {
            builder.addField("YouTube Link", "https://youtu.be/" + youtubeLink, false);
        }
        if (missionPatch != null) {
            builder.setThumbnail(missionPatch);
        }
        builder.addField("Rocket", rocketName, false);
        builder.addField("Launch Site", launchSite, false);

        return builder;
    }

    private void sendRequest(TextChannel channel) {        
        try {            
            channel.sendMessage(getBuilder().build()).queue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            channel
                    .sendMessage(
                            getEmbedSimpleError("Unexpected Exception", "Error message: " + e.getMessage()).build())
                    .queue();
        } catch (ParseException pe) {
            pe.printStackTrace();
            channel.sendMessage(getEmbedSimpleError("Parse Exception",
                    "Unexpected response.")
                            .build())
                    .queue();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            channel
                    .sendMessage(getEmbedSimpleError("Null Pointer Exception",
                            "Unexpected null pointer exception.").build())
                    .queue();
        } catch (Exception e) {
            channel.sendMessage(getEmbedSimpleError("Generic Exception", "Unidentified exception.").build()).queue();
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        sendRequest(event.getTextChannel());
    }
}