package com.falconxrobotics.discordbot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import com.falconxrobotics.discordbot.commands.Dice;
import com.falconxrobotics.discordbot.commands.Help;
import com.falconxrobotics.discordbot.commands.Javadoc;
import com.falconxrobotics.discordbot.commands.LaTex;
import com.falconxrobotics.discordbot.commands.Last;
import com.falconxrobotics.discordbot.commands.Ping;
import com.falconxrobotics.discordbot.commands.Poll;
import com.falconxrobotics.discordbot.commands.Pray;
import com.falconxrobotics.discordbot.commands.Rainbow;
import com.falconxrobotics.discordbot.commands.SelfAssign;
import com.falconxrobotics.discordbot.commands.User;
import com.falconxrobotics.discordbot.commands._Guild;
import com.falconxrobotics.discordbot.commands.corona.Coronavirus;
import com.falconxrobotics.discordbot.commands.eval.Evaluate;
import com.falconxrobotics.discordbot.commands.moderation.Mute;
import com.falconxrobotics.discordbot.commands.moderation.Nick;
import com.falconxrobotics.discordbot.commands.moderation.Purge;
import com.falconxrobotics.discordbot.commands.moderation.Unmute;
import com.falconxrobotics.discordbot.commands.music.Music;
import com.falconxrobotics.discordbot.commands.reddit.Reddit;
import com.falconxrobotics.discordbot.commands.spacex.SpaceX;
import com.falconxrobotics.discordbot.commands.test.Test;
import com.github.raybipse.components.SimpleCommand;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The beginning point of the application.
 */
public class Bot extends ListenerAdapter {

    private static JDA jda;

    private Bot() {
        jda.addEventListener(this);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        if (System.getenv("BOT_TOKEN") == null) {
            throw new LoginException("BOT_TOKEN environment variable must be set for the bot token.");
        }

        jda = JDABuilder.createDefault(System.getenv("BOT_TOKEN")).build().awaitReady();
        BotConfiguration.setJDA(jda);
        try {
            initAllCommands();
            jda.getPresence()
                    .setActivity(Activity.listening(BotConfiguration.getBotPrefix() + Help.getInstance().getPrefix()));
        } catch (NullPointerException irte) {
            jda.shutdown();
            irte.printStackTrace();
            System.exit(1);
        }

        // for (GuildChannel channel : jda.getGuildById("384880977773854720").getChannels()) {
            // System.out.println(channel.getName());
        // }

        // for (Member m : jda.getGuildById("384880977773854720").getMembers()) {
        //     jda.getGuildById("384880977773854720").removeRoleFromMember(m, jda.getRoleById("611747175944159232")).queue();
        // }

        // jda.getTextChannelById("533912803048685578").sendMessage(GetStringArray(jda.getGuildById("384880977773854720").getChannels())).queue();
    }

    public static String[] GetStringArray(ArrayList<String> arr) 
    { 
  
        // declaration and initialise String Array 
        String str[] = new String[arr.size()]; 
  
        // ArrayList to Array Conversion 
        for (int j = 0; j < arr.size(); j++) { 
  
            // Assign each value to String array 
            str[j] = arr.get(j); 
        } 
  
        return str; 
    } 
  

    public static String formatNumber(String input) {
        try {
            double num = Double.parseDouble(input);
            return NumberFormat.getInstance().format(num);
        } catch (NumberFormatException nfe) {
            return input;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().isMentioned(jda.getSelfUser(), MentionType.USER)) {
            event.getChannel().sendMessage("Use &help").queue();
        }
    }

    public static void initAllCommands() {
        Test.getInstance();
        Reddit.getInstance();
        Music.getInstance();
        Coronavirus.getInstance();
        Evaluate.getInstance();
        // SpaceX.getInstance();
        new Dice();
        new LaTex();
        new Ping();
        new Pray();
        new Poll();
        new Last();
        new Javadoc();
        new User();
        new Rainbow();
        new _Guild();
        new Mute();
        new Unmute();
        new Nick();
        new Purge();
        new Bot();
        new SelfAssign();
        // new Rotate();
        SimpleCommand source = new SimpleCommand("Source", "source", "Gets the source of the bot's code.",
                List.of(""), "", null);

        source.withMessageReceivedEvent(
                (evt) -> evt.getChannel().sendMessage("https://github.com/FalconX-Robotics/falkim-bot").queue(),
                List.of((event) -> !event.getAuthor().isBot()
                        && source.getInputValidity(event.getMessage().getContentDisplay())));

        SimpleCommand dab = new SimpleCommand("Dab", "dab", "Dabs.",
                List.of(""), "", null);

        dab.withMessageReceivedEvent(
                (evt) -> evt.getChannel().sendMessage("<:SteveDabbing:598269423035482113>").queue(),
                List.of((event) -> !event.getAuthor().isBot()
                        && dab.getInputValidity(event.getMessage().getContentDisplay())));

        Help.getInstance();
    }
}
