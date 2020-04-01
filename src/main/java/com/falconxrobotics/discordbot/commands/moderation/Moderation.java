package com.falconxrobotics.discordbot.commands.moderation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class Moderation {

    private static Moderation instance;

    public Map<String, Timer> muteTimers = new HashMap<>();

    public Moderation() {
    }

    /**
     * @return the sole instance of the class
     */
    public static Moderation getInstance() {
        if (instance == null) {
            instance = new Moderation();
        }
        return instance;
    }

    public enum TimeType {
        SECOND("s", 1, "seconds"), MINUTE("m", 60, "minutes"), HOUR("h", 60 * 60, "hours"), DAYS("d", 60 * 60 * 24, "days");

        public final String id;
        public final long amount; 
        public final String display;
        
        private TimeType(String id, long amount, String display) {
            this.id = id;
            this.amount = amount;
            this.display = display;
        }

        public static TimeType findType(String id) {
            for (TimeType type : values()) {
                if (id.equalsIgnoreCase(type.id)) {
                    return type;
                }
            }
            return null;
        }
    }

    public void setUpTimer(String id, Runnable runnable, long milliseconds) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                runnable.run();
            }
        }, milliseconds);
        muteTimers.put(id, timer);
    }

    public Role getMutedRole(Guild guild) {
        List<Role> roles = guild.getRolesByName("Muted", false);
        if (roles.isEmpty()) {
            Role role = guild.createRole().setName("Muted").setPermissions(Permission.EMPTY_PERMISSIONS).setMentionable(false).complete();
            for (TextChannel channel : guild.getTextChannels()) {
                channel.createPermissionOverride(role).deny(Permission.MESSAGE_WRITE).queue();
            }
            return role;
        } else {
            return roles.get(0);
        }
    }
}