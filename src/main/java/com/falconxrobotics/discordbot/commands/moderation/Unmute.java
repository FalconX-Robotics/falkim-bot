package com.falconxrobotics.discordbot.commands.moderation;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;

/**
 * @author RayBipse
 */
public class Unmute extends Command {

    public Unmute() {
        super("Unmute", "unmute");
        setDescription("Unmutes a noob.");
        addExamples("@noob", "@noob @noo2");
        setSyntax("[members]");

    }

    private void sendInvalidUser(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
            getEmbedInvalidParameterError("User Not Found")
                .addField("Note", "The user id provided must be in the server", false)
                .build()
        ).queue();
    }

    private void unmute(Member member) {
        var timers = Moderation.getInstance().muteTimers;
        Timer timer;
        if ((timer = timers.get(member.getId())) != null) {
            timer.cancel();
            timers.remove(member.getId());
        }
        member.getGuild().removeRoleFromMember(member, Moderation.getInstance().getMutedRole(member.getGuild())).queue();
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

        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS) && !(event.getAuthor().getIdLong() == 239501588589182987L)) {
            event.getChannel().sendMessage(getEmbedSimpleError("Permission Not Met", "You must have ``kick member`` permission.").build()).queue();
            return;
        }

        if (arguments.length == 0) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            List<Member> members = event.getMessage().getMentionedMembers(event.getGuild());
            if (!members.isEmpty()) {

                try {
                    for (Member member : members) {
                        unmute(member);
                    }
                } catch (Exception e) {
                    event.getChannel().sendMessage(getEmbedSimpleError("Permission Error", "The bot does not have sufficient permission to unmute user.").build()).queue();
                    return;
                }
                
                event.getChannel().sendMessage(
                    new EmbedBuilder()
                        .setTitle("Unmuted: " + Arrays.toString(members.stream().map(
                            (m) -> {
                                return m.getUser().getName() + m.getUser().getDiscriminator();
                            }
                        ).toArray(String[]::new)))
                        .setColor(Color.YELLOW)
                        .build()
                ).queue();
            } else {
                sendInvalidUser(event);
            }
        }
    }

}