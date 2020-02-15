package com.falconxrobotics.discordbot.commands.reddit;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.CommandGroup;

/**
 * A command group that contains commands that are used for interactions with Reddit.
 * 
 * @author RayBipse
 */
public class Reddit extends CommandGroup {
    private static Reddit instance;
    private Reddit.Help help = new Reddit.Help();
    private Top top = new Top();

    private Reddit() {
    }

    /**
     * @return the sole instance of the class
     */
    public static Reddit getInstance() {
        if (instance == null) {
            instance = new Reddit();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Reddit";
    }

    @Override
    public String getDescription() {
        return "Commands that involves interacting with Reddit.";
    }

    @Override
    public Command[] getChildren() {
        return new Command[] { help, top };
    }

    @Override
    public String getPrefix() {
        return "reddit";
    }
}
