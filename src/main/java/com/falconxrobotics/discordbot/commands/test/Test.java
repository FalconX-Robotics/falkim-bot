package com.falconxrobotics.discordbot.commands.test;

import com.github.raybipse.components.Command;
import com.github.raybipse.components.CommandGroup;

/**
 * A command group that contains commands that are used for testing and
 * debugging the application.
 * 
 * @author RayBipse
 */
public class Test extends CommandGroup {
    private static Test instance;
    private Test.Help help = new Test.Help();
    private Echo echo = new Echo();
    private Cool cool = new Cool();
    private EchoTo echoto = new EchoTo();

    private Test() {
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

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public String getDescription() {
        return "Commands are used for testing and debugging the application.";
    }

    @Override
    public Command[] getChildren() {
        return new Command[] { help, echo, echoto, cool };
    }

    @Override
    public String getPrefix() {
        return "test";
    }
}
