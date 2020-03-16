package com.falconxrobotics.discordbot.commands;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * UserInfo
 */
public class UserInfo extends Command {

    public UserInfo() {
        super("UserInfo", "userinfo");
        setDescription("Returns details and general information about the bot.");
        setSyntax("[optional: user id; default: invoker]");
        addExamples("", "239501588589182987", "@Ray#6714");
    }

    private void sendInvalidUser(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
            getEmbedInvalidParameterError("User Not Found")
                .addField("Note", "The user id provided must be in the server", false)
                .build()
        ).queue();
    }

    private EmbedBuilder getMemberInfo(Member member) {
        return new EmbedBuilder()
            .setTitle("Member: " + member.getUser().getName() + "#" + member.getUser().getDiscriminator())
            .setThumbnail(member.getUser().getAvatarUrl())
            .addField("Join Date", member.getTimeJoined().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), true)
            .addField("Registration Date", member.getUser().getTimeCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)), true)
            .addField("Roles (" + member.getRoles().size() + ")", Arrays.toString(member.getRoles().stream().map(Role::getName).toArray(String[]::new)), false)
            .addField("ID", member.getUser().getId(), true)
            .addField("Is Owner", String.valueOf(member.isOwner()), true)
            .addField("Is Bot", String.valueOf(member.getUser().isBot()), true)
            .addField("Nickname", member.getEffectiveName(), true);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        
        Member member;
        if (messageContent.length() == 0) {
            member = event.getMember();
        } else {
            try {
                System.out.println(messageContent);
                member = event.getGuild().getMemberById(messageContent);
                if (member == null) {
                    member = event.getGuild().getMembersByEffectiveName(messageContent, false).get(0);
                    if (member == null) {
                        sendInvalidUser(event);
                        return;
                    }
                }
            } catch (NumberFormatException nfe) {
                var members = event.getGuild().getMembersByEffectiveName(messageContent.substring(1), false);
                if (members.size() == 0) {
                    sendInvalidUser(event);
                    return;
                } else {
                    member = members.get(0);
                }
            }
        }
        event.getChannel().sendMessage(getMemberInfo(member).build()).queue();
    }
    
}