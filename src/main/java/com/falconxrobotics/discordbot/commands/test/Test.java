package com.falconxrobotics.discordbot.commands.test;

import com.github.raybipse.components.CommandGroup;

/**
 * A command group that contains commands that are used for testing and
 * debugging the application.
 * 
 * @author RayBipse
 */
public class Test extends CommandGroup {
    private static Test instance;
    private Echo echo = new Echo();
    private Cool cool = new Cool();
    private EchoTo echoto = new EchoTo();

    private Test() {
        super("Test", "test");
        setDescription("Commands are used for testing and debugging the application.");
        addChildren(echo, echoto, cool, new Help());
    }

    /**
     * @return the sole instance of the class
     */
    public static Test getInstance() {
        if (instance == null) {
            instance = new Test();
        }
        return instance;
    }
}
