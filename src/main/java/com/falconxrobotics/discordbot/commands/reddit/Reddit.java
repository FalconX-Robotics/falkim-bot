package com.falconxrobotics.discordbot.commands.reddit;

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
        super("Reddit", "reddit");
        setDescription("Commands that involves interacting with Reddit.");
        addChildren(help, top);
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
}
