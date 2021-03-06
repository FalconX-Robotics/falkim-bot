package com.falconxrobotics.discordbot.commands.reddit;

import com.github.raybipse.components.CommandGroup;

/**
 * @author RayBipse
 */
public class Reddit extends CommandGroup {
    private static Reddit instance;
    private Top top = new Top();

    private Reddit() {
        super("Reddit", "reddit");
        setDescription("Commands that involves interacting with Reddit.");
        addChildren(top, new Help());
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
