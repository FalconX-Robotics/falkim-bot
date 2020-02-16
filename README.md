### About
This is the source code for the discord bot, "Peter Kim Jr.", which operates in the FalconX Robotics discord.

### Dependencies
- [jda command bot](https://github.com/RayBipse/jda-command-bot)
- [jda](https://github.com/DV8FromTheWorld/JDA)
- [json simple](https://github.com/fangyidong/json-simple)

### Setup
#### Environment variable
Add an environment variable called ``BOT_TOKEN``, which contains the bot token for the discord bot.
To run the program with the env variable on VSCode, specify this in ``.vscode/launch.json``
```
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug (Launch) - Current File",
            "request": "launch",
            "mainClass": "${file}"
        },
        {
            "type": "java",
            "name": "Debug (Launch)-Bot<falkim-bot>",
            "request": "launch",
            "mainClass": "com.falconxrobotics.discordbot.Bot",
            "projectName": "falkim-bot",
            "env": {
                "BOT_TOKEN": <TOKEN HERE>
            }
        }
    ]
}
```

#### Deploy to heroku
```bash
mvn clean heroku:deploy
```

### Warning
Never push the bot token onto git.