package com.falconxrobotics.discordbot.commands.spacex;

import com.github.raybipse.components.CommandGroup;

/**
 * A command group that contains commands that are used for interactions with Reddit.
 * 
 * @author RayBipse
 */
public class SpaceX extends CommandGroup {
    private static SpaceX instance;
    private Next next = new Next();

    private SpaceX() {
        super("SpaceX", "spacex");
        setDescription("Commands that involves interacting with the SpaceX API.");
        addChildren(next, new Help());
    }

    /**
     * @return the sole instance of the class
     */
    public static SpaceX getInstance() {
        if (instance == null) {
            instance = new SpaceX();
        }
        return instance;
    }
}
