package com.falconxrobotics.discordbot.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command that supplies a random value.
 * 
 * @author RayBipse
 */
public class SelfAssign extends Command {

    public SelfAssign() {
        super("Self Assign", "selfassign");
        setDescription("Gives a role to a member. Assignable roles: " + Arrays.toString(assignableRoleNames));
        addExamples("Programming", "Mechanical");
        setSyntax("[role name]");

        assignableRoles.add("479511573811953674");
        assignableRoles.add("479511021598277632");
        assignableRoles.add("481962341445926913");
        assignableRoles.add("554162174088708096");
        assignableRoles.add("696819469216907305");
    }

    private String[] assignableRoleNames = new String[] {
        "Business", "Mechanical", "Programming", "Electrical", "Learning CAD"
    };

    private HashSet<String> assignableRoles = new HashSet<String>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent).trim();        

        if (!event.getGuild().getId().equals("384880977773854720")) {
            event.getChannel().sendMessage(getEmbedSimpleError("Wrong Guild", "You cannot use this command in this guild.").build()).queue();
            return;
        }

        for (Role role : event.getGuild().getRoles()) {
            if (role.getName().equalsIgnoreCase(messageContent)) {
                if (assignableRoles.contains(role.getId())) {
                    event.getGuild().addRoleToMember(event.getMember(), role).queue(m -> {
                        event.getChannel().sendMessage("Successful").queue();
                    }, (e) -> {
                        event.getChannel().sendMessage("No Permission").queue();
                    });
                } else {
                    event.getChannel().sendMessage("Role is not assignable").queue();
                }
                return;
            }
        }
        event.getChannel().sendMessage("Role not found").queue();
    }
}