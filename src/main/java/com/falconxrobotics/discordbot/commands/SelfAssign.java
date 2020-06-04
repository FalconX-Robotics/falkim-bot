package com.falconxrobotics.discordbot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.github.raybipse.components.Command;
import com.github.raybipse.core.BotConfiguration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/**
 * @author RayBipse
 */
public class SelfAssign extends Command {

    private static String[] emojis = new String[] {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"};

    private String[] assignableRoleNames = new String[] {
        "Business", "Mechanical", "Programming", "Electrical", "Learning CAD"
    };

    private String[] assignableRoles = new String[] {
        "479511573811953674", "479511021598277632", "481962341445926913", "554162174088708096", "696819469216907305"
    };

    private String guildId = "384880977773854720";
    private String roleChannelId = "718171135346475109";
    private Message roleMsg = null;

    // private HashSet<String> assignableRoles = new HashSet<String>();

    public SelfAssign() {
        super("Self Assign", "selfassign");
        setDescription("Gives a role to a member. Assignable roles: " + Arrays.toString(assignableRoleNames));
        addExamples("Programming", "Mechanical");
        setSyntax("[role name]");

        // assignableRoles.add("479511573811953674");
        // assignableRoles.add("479511021598277632");
        // assignableRoles.add("481962341445926913");
        // assignableRoles.add("554162174088708096");
        // assignableRoles.add("696819469216907305");

        if (assignableRoleNames.length != assignableRoles.length) {
            throw new RuntimeException("Bruh.");
        }

        setupReactions();
    }

    private Consumer<Message> addReactionConsumer = m -> {
        roleMsg = m;
        for (int i = 0; i < assignableRoleNames.length; i++) {
            m.addReaction(emojis[i]).queue();
        }
    };

    private void replaceOld(EmbedBuilder builder) {
        TextChannel channel = BotConfiguration.getJDA().getTextChannelById(roleChannelId);
        channel.getHistory().retrievePast(1).queue(history -> {
            if (history.isEmpty() || !history.get(0).getAuthor().getId().equals(BotConfiguration.getJDA().getSelfUser().getId())) {
                channel.sendMessage(builder.build()).queue(addReactionConsumer);
            } else {
                history.get(0).editMessage(builder.build()).queue(addReactionConsumer);
            }
        });
        
    }

    private void setupReactions() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Role Signup");
        builder.setDescription("React to the corresponding reaction to self-assign your desired role. If you already have the role, reacting again will remove it.");
        for (int i = 0; i < assignableRoleNames.length; i++) {
            builder.addField(emojis[i], assignableRoleNames[i], false);
        }
        builder.setFooter("If this does not work, use \"" + BotConfiguration.getBotPrefix() + this.getPrefix() + "\".");

        replaceOld(builder);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot() || !event.isFromGuild() || !event.getChannel().getId().equals(roleChannelId)) {
            return;
        }
        if (!event.getMessageId().equals(roleMsg.getId())) {
            return;
        }
        String emoteName = event.getReactionEmote().getEmoji();
        for (int i = 0; i < assignableRoleNames.length; i++) {
            if (emoteName.equals(emojis[i])) {

                // remove if already have
                Role role = event.getGuild().getRoleById(assignableRoles[i]);
                if (event.getMember().getRoles().contains(role)) {
                    event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                } else {
                    event.getGuild().addRoleToMember(event.getMember(), role).queue();
                }

                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }

    private <T> boolean contains(T[] arr, T e) {
        for (T v : arr) {
            if (v.equals(e)) return true;
        }
        return false;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent).trim();        

        if (!event.getGuild().getId().equals(guildId)) {
            event.getChannel().sendMessage(getEmbedSimpleError("Wrong Guild", "You cannot use this command in this guild.").build()).queue();
            return;
        }

        for (Role role : event.getGuild().getRoles()) {
            if (role.getName().equalsIgnoreCase(messageContent)) {
                if (contains(assignableRoles, role.getId())) {
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