package com.falconxrobotics.discordbot.commands;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class User extends Command {

    public User() {
        super("User", "user");
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
            .addField("Is Administrator", String.valueOf(member.getPermissions().contains(Permission.ADMINISTRATOR)), true)
            .addField("Nickname", member.getEffectiveName(), true)
            .addField("Permissions (" + member.getPermissions().size() + ")", member.getPermissions().toString(), false)
            .setColor(BotConfiguration.getPromptColor());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        
        List<Member> members;
        if (messageContent.length() == 0) {
            members = List.of(event.getMember());
        } else {
            try {
                members = event.getMessage().getMentionedMembers(event.getGuild());
            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage(getEmbedInvalidParameterError("Guild Not Found").build()).queue();
                return;
            }
        }
        if (!members.isEmpty()) 
            members.stream().forEach(m -> event.getChannel().sendMessage(getMemberInfo(m).build()).queue());
        else 
            sendInvalidUser(event);
    }
    
}