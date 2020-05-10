package com.falconxrobotics.discordbot.commands.corona;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.github.raybipse.components.CommandGroup;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.entities.TextChannel;

public class Coronavirus extends CommandGroup {

    private static Coronavirus instance;
    private In top = new In();
    private All all = new All();

    private Timer timer;
    private int targetTimerHour = 12;
    private int targetTimerMinute = 0;

    private Coronavirus() {
        super("Coronavirus", "corona");
        setDescription("Gives information about COVID-19.");
        addChildren(top, all, new Help());
        setUpTimer();
    }

    private void setUpTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            private TextChannel channel = BotConfiguration.getJDA().getTextChannelById("533820195949510666");

            public void run() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Date.from(Instant.now()));
                cal.setTimeZone(TimeZone.getTimeZone("PST"));
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                if ((hour == targetTimerHour) && minutes == targetTimerMinute) {
                    sendMessage();
                }
            }

            public void sendMessage() {
                channel.sendMessage("COVID-19 update")
                    .queue(m -> {
                        channel.sendMessage(all.invoke(null).build()).queue();;
                        channel.sendMessage(top.invoke("USA").build()).queue();
                    });
            }
        };
        timer.schedule(task, 0, 60 * 1000);
    }

    /**
     * @return the sole instance of the class
     */
    public static Coronavirus getInstance() {
        if (instance == null) {
            instance = new Coronavirus();
        }
        return instance;
    }
}