package com.falconxrobotics.discordbot;

import javax.security.auth.login.LoginException;

import com.falconxrobotics.discordbot.commands.Dice;
import com.falconxrobotics.discordbot.commands.Help;
import com.falconxrobotics.discordbot.commands.LaTex;
import com.falconxrobotics.discordbot.commands.Ping;
import com.falconxrobotics.discordbot.commands.music.Music;
import com.falconxrobotics.discordbot.commands.reddit.Reddit;
import com.falconxrobotics.discordbot.commands.test.Test;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

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
            Reddit.getInstance();
            Music.getInstance();
            new Dice();
            new LaTex();
            new Ping();
            Help helpCommand = new Help();

            jda.getPresence().setActivity(Activity.listening(BotConfiguration.getBotPrefix() + helpCommand.getPrefix()));
        } catch (NullPointerException irte) {
            jda.shutdown();
            irte.printStackTrace();
            System.exit(1);
        }
    }

    // public static CommandGroup[] getAllCommandGroups() {
    //     return new CommandGroup[] {
    //         Test.getInstance(), Reddit.getInstance()
    //     };
    // }

    // public static Command[] getAllStandaloneCommands() {
    //     return new Command[] {
    //         helpCommand, dice, latex, ping
    //     };
    // }
}
