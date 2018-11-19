package com.alibaba.nls.client.example;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.InputStream;

/**
 * @Author ryan.song
 * @Date 2018/11/13
 **/
public class AudioPlayerTest {
    public static void main(String[] args) {

        try {
            InputStream in = AudioPlayerTest.class.getResourceAsStream("/jimo.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
            AudioFormat audioFormat = audioInputStream.getFormat();
            System.out.println(audioFormat);
        }catch (Exception e){
            e.printStackTrace();
        }





    }
}
