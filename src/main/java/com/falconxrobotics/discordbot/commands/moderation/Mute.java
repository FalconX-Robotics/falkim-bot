package com.falconxrobotics.discordbot.commands.moderation;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.falconxrobotics.discordbot.commands.moderation.Moderation.TimeType;
import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Mute extends Command {

    public Mute() {
        super("Mute", "mute");
        setDescription("Mutes a noob.");
        addExamples("@noob 1h", "@noob @noo2 2h");
        setSyntax("[members] [time]");

    }

    public Role getMutedRole(Guild guild) {
        List<Role> roles = guild.getRolesByName("Muted", false);
        if (roles.isEmpty()) {
            Role role = guild.createRole().setName("Muted").setPermissions(Permission.EMPTY_PERMISSIONS)
                    .setMentionable(false).complete();
            for (TextChannel channel : guild.getTextChannels()) {
                channel.createPermissionOverride(role).deny(Permission.MESSAGE_WRITE).queue();
            }
            return role;
        } else {
            return roles.get(0);
        }
    }

    private void sendInvalidUser(MessageReceivedEvent event) {
        event.getChannel().sendMessage(getEmbedInvalidParameterError("User Not Found")
                .addField("Note", "The user id provided must be in the server", false).build()).queue();
    }

    private void mute(Member member, long seconds) {
        member.getGuild().addRoleToMember(member, getMutedRole(member.getGuild())).queue();
        Moderation.getInstance().setUpTimer(member.getId(), () -> {
            member.getGuild().removeRoleFromMember(member, getMutedRole(member.getGuild())).queue();
            ;
        }, seconds * 1000);
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

        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)
                && !(event.getAuthor().getIdLong() == 239501588589182987L)) {
            event.getChannel().sendMessage(
                    getEmbedSimpleError("Permission Not Met", "You must have ``kick member`` permission.").build())
                    .queue();
            return;
        }

        if (arguments.length <= 1) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            List<Member> members = event.getMessage().getMentionedMembers(event.getGuild());
            if (!members.isEmpty()) {
                String lastArg = arguments[arguments.length - 1];
                long amount = 0;
                TimeType type = TimeType.findType(lastArg.substring(lastArg.length() - 1, lastArg.length()));
                if (type == null)
                    type = TimeType.MINUTE;
                try {
                    amount = Long.valueOf(lastArg.substring(0, lastArg.length() - 1));
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterTypes().build()).queue();
                    return;
                }

                if (amount < 0) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterError("Amount Is Negative").build()).queue();
                    return;
                } else if (amount * type.amount > 60 * 60 * 24 * 7) {
                    event.getChannel().sendMessage(getEmbedInvalidParameterError("Amount Is More Than 1 Week").build())
                            .queue();
                    return;
                }

                try {
                    for (Member member : members) {
                        mute(member, amount * type.amount);
                    }
                } catch (Exception e) {
                    event.getChannel().sendMessage(getEmbedSimpleError("Permission Error", "The bot does not have sufficient permission to mute user.").build()).queue();
                    return;
                }

                event.getChannel().sendMessage(new EmbedBuilder().setTitle("Muted: " + Arrays
                        .toString(members.stream().map((m) -> m.getUser().getName() + m.getUser().getDiscriminator()

                        ).toArray(String[]::new)))
                        .addField("Amount",
                                String.valueOf(amount) + " "
                                        + type.display.substring(0,
                                                (amount == 1 ? type.display.length() - 1 : type.display.length())),
                                false)
                        .setColor(Color.YELLOW).build()).queue();
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