package com.falconxrobotics.discordbot.commands.music;

import com.github.raybipse.components.Command;
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

    private Music() {
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

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public String getDescription() {
        return "Commands that is used to play music.";
    }

    @Override
    public Command[] getChildren() {
        return new Command[] { help, play };
    }

    @Override
    public String getPrefix() {
        return "m";
    }

}