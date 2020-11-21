package com.falconxrobotics.discordbot.commands;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.github.raybipse.components.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author RayBipse
 */
public class Rotate extends Command {

    public Rotate() {
        super("Rotate", "rotate");
        setDescription("Rotates an image.");
        addExamples("90");
        setSyntax("[deg] [optional: url; default: last image sent in channel]");
    }

    public static BufferedImage rotateCw(BufferedImage img) {
        int width  = img.getWidth();
        int height = img.getHeight();
        BufferedImage newImage = new BufferedImage(height, width, img.getType());

        for (int i=0; i < width; i++)
            for (int j=0; j < height; j++)
                newImage.setRGB(height-1-j, i, img.getRGB(i,j));

        return newImage;
    }

    private BufferedImage getLastImage() throws IOException {
        URL url = new URL("https://i.kym-cdn.com/entries/icons/original/000/016/540/hgh08Pez.jpeg");
        Image img = ImageIO.read(url);
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (event.getAuthor().isBot())
            return;
        if (!getInputValidity(messageContent))
            return;

        messageContent = trimInputBeginning(messageContent);
        String[] arguments = splitUserInput(messageContent);


        if (arguments.length == 0) {
            event.getChannel().sendMessage(getEmbedMissingArguments().build()).queue();
        } else {
            BufferedImage img;
            try {
                img = getLastImage();
                img = rotateCw(img);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", baos);
                event.getChannel().sendFile(baos.toByteArray(), "hi").queue();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}