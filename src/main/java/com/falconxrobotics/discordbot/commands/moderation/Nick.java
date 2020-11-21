package com.falconxrobotics.discordbot.commands.moderation;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Nick extends Command {

    public Nick() {
        super("Nick", "nick");
        setDescription("Sets the nickname of the specified member.");
        addExamples("@noob", "@noob @noo2 stupid");
        setSyntax("[members] [name]");
    }

    private void sendInvalidUser(MessageReceivedEvent event) {
        event.getChannel().sendMessage(getEmbedInvalidParameterError("User Not Found")
                .addField("Note", "The user id provided must be in the server", false).build()).queue();
    }

    private void rename(Member member, String arg) {
        member.getGuild().modifyNickname(member, arg).queue();
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

        if (!event.getMember().hasPermission(Permission.NICKNAME_MANAGE)
                && !(event.getAuthor().getIdLong() == 590931976182169600L)) {
            event.getChannel().sendMessage(
                    getEmbedSimpleError("Permission Not Met", "You must have ``manage nickname`` permission.").build())
                    .queue();
            return;
        }

        if (arguments.length <= 1) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            List<Member> members = event.getMessage().getMentionedMembers(event.getGuild());
            if (!members.isEmpty()) {
                String lastArg = arguments[arguments.length - 1];

                try {
                    for (Member member : members) {
                        rename(member, lastArg);
                    }
                } catch (Exception e) {
                    event.getChannel().sendMessage(getEmbedSimpleError("Permission Error", "The bot does not have sufficient permission to change the users' nickname.").build()).queue();
                    return;
                }

                event.getChannel().sendMessage(
                        new EmbedBuilder().setTitle("Nicknamed: " + Arrays.toString(members.stream().map((m) -> {
                            return m.getUser().getName() + m.getUser().getDiscriminator();
                        }).toArray(String[]::new)))
                                .addField("Before",
                                        Arrays.toString(
                                                members.stream().map(Member::getEffectiveName).toArray(String[]::new)),
                                        false)
                                .addField("After", lastArg, false).setColor(Color.YELLOW).build())
                        .queue();
            } else {
                sendInvalidUser(event);
            }
        }
    }

    @Override
    public EmbedBuilder getEmbedInfo() {
        EmbedBuilder builder = super.getEmbedInfo();
        builder.addField("Time Parameter", "[number][s OR h OR d]", false);
        builder.addField("Time Types", "``s`` means seconds\n``m`` is minutes\n``h`` means hours\n``d`` means days.",
                false);
        return builder;
    }

}