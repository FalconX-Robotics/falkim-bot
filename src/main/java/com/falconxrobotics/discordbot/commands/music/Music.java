package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.CommandGroup;

/**
 * A command group that contains commands that is used to play music.
 * 
 * @author RayBipse
 */
public class Music extends CommandGroup {
    private static Music instance;
    private Music.Help help = new Music.Help();
    private Play play = new Play();

    public Music() {
        super("Music", "m");
        setDescription("Commands that is used to play music.");
        addChildren(help, play);
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