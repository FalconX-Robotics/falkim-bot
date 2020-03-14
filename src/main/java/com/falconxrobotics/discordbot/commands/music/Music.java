package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.CommandGroup;

/**
 * A command group that contains commands that is used to play music.
 * 
 * @author RayBipse
 */
public class Music extends CommandGroup {
    private static Music instance;
    private Play play = new Play();

    protected Music() {
        super("Music", "m");
        setDescription("Commands that is used to play music.");
        addChildren(play, new Help());
    }

    /**
     * @return the sole instance of the class
     */
    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }
}