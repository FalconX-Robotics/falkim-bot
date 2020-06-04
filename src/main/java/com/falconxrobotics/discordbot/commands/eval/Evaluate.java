package com.falconxrobotics.discordbot.commands.eval;

import com.github.raybipse.components.CommandGroup;

/**
 * @author RayBipse
 */
public class Evaluate extends CommandGroup {
    private static Evaluate instance;
    private JS js = new JS();

    public Evaluate() {
        super("Evaluate", "eval");
        setDescription("Commands that are used to evaluate code.");
        addChildren(js, new Help());
    }

    /**
     * @return the sole instance of the class
     */
    public static Evaluate getInstance() {
        if (instance == null) {
            instance = new Evaluate();
        }
        return instance;
    }
}