package com.ling.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class AuthCodeUtil {
    private static final int AUTHCODE_LENGTH = 5; // length of verification code
    private static final int SINGLECODE_WIDTH = 10; // width of one digit or character in the image
    private static final int SINGLECODE_HEIGHT = 25; // height of one digit or character in the image
    private static final int SINGLECODE_GAP = 5; // margin of a digit or character
    private static final int PADDING_TOP_BOT = 10;// padding of top and bottom
    private static final int PADDING_LEFT_RIGHT = 10; //padding of left and right
    private static final int IMG_WIDTH = AUTHCODE_LENGTH * (SINGLECODE_WIDTH + SINGLECODE_GAP) + PADDING_LEFT_RIGHT;
    private static final int IMG_HEIGHT = SINGLECODE_HEIGHT+PADDING_TOP_BOT;
    private static final char[] CHARS = {'0','1', '2', '3', '4', '5', '6', '7', '8',
            '9','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static Random random = new Random();


    public static String getAuthCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {// generate 6 digit and character
            builder.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return builder.toString();
    }


    public static BufferedImage getAuthImg(String authCode) {
        BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT,
                BufferedImage.TYPE_INT_BGR);

        Graphics g = img.getGraphics();
        g.setColor(new Color(255, 255, 230));
        g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, SINGLECODE_HEIGHT + 5));
        char c;
        for (int i = 0; i < authCode.toCharArray().length; i++) {

            c = authCode.charAt(i);

            g.drawString(c + "", i * (SINGLECODE_WIDTH + SINGLECODE_GAP)
                    + SINGLECODE_GAP / 2 +PADDING_LEFT_RIGHT/2, IMG_HEIGHT-PADDING_TOP_BOT/2);
        }
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(IMG_WIDTH);
            int y = random.nextInt(IMG_HEIGHT);
            int x2 = random.nextInt(IMG_WIDTH);
            int y2 = random.nextInt(IMG_HEIGHT);
            g.drawLine(x, y, x + x2, y + y2);
        }
        return img;
    }
}