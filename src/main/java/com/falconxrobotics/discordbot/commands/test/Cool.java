package com.falconxrobotics.discordbot.commands.test;

import java.util.Set;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 * A temporary command that is used to test new features.
 * 
 * @author RayBipse
 */
public class Cool extends Command {

    protected Cool() {
        super("Cool", "cool");
        setDescription("Gets a message regarding with if whether you are cool or not.");
        setSyntax("");
        setParent(Test.getInstance());

        if (BotConfiguration.getJDA().getGuildById(384880977773854720L) != null) {
            requireRoles(Set.<Role>of(BotConfiguration.getJDA().getGuildById(384880977773854720L).getRoleById(481962341445926913L)));
            setOnRolePermissionFail((event) -> 
                    event.getChannel().sendMessage(new EmbedBuilder()
                        .setDescription("Did you SERIOUSLY think you're cool? You're pathetic.")
                        .setColor(BotConfiguration.getErrorColor())
                        .build()).queue());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;
        try {
            enforceUserRolePermission(event);
        } catch (PermissionException pe) {
            pe.printStackTrace();
            return;
        }

        event.getChannel().sendMessage(new EmbedBuilder().setDescription("Nice! You're a programmer. You're cool.").build()).queue();
    }
}