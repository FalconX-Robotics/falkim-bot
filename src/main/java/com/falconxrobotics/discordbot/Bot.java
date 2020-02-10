package com.falconxrobotics.discordbot;

import javax.security.auth.login.LoginException;

import com.github.raybipse.core.BotConfiguration;
import com.falconxrobotics.discordbot.commands.test.Test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * The beginning point of the application.
 */
public class Bot {

    private static JDA jda;

    private Bot() {
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        if (System.getenv("BOT_TOKEN") == null) {
            throw new LoginException("BOT_TOKEN environment variable must be set for the bot token.");
        }

        JDABuilder jdaBuilder = new JDABuilder(System.getenv("BOT_TOKEN"));
        jda = jdaBuilder.build().awaitReady();
        BotConfiguration.setJDA(jda);

        try {
            // Instantiate your commands and command groups here
            // Do not instantiate commands before jda.awaitReady() is ran
            Test.getInstance();
        } catch (NullPointerException irte) {
            jda.shutdown();
            irte.printStackTrace();
            System.exit(1);
        }
    }
}
