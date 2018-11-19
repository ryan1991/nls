package com.alibaba.nls.client.example.demo;

import com.alibaba.nls.client.example.SpeechProperties;
import com.alibaba.nls.client.example.SpeechTranscriberDemo;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Author ryan.song
 * @Date 2018/11/13
 **/
public class TestALYSynthesizer {
    public static void main(String[] args) throws Exception {


        TargetDataLine targetDataLine = AudioUtils.getTargetDataLine();
        SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();
        byte[] buf = new byte[2048];
        InputStream in = null;
        while (true){

            int  i = targetDataLine.read(buf, 0 , buf.length);
            in = new ByteArrayInputStream(buf);

            sourceDataLine.write(buf, 0, i);

            String appKey = SpeechProperties.APPKEY;
            String token = SpeechProperties.TOKEN;

            SpeechTranscriberDemo demo = new SpeechTranscriberDemo(appKey, token);
            if (null == in) {
                System.err.println("open the audio file failed!");
                return;
            }
            demo.process();

        }





    }



}
