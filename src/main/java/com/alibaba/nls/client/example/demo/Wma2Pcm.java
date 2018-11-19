package com.alibaba.nls.client.example.demo;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;

/**
 * @Author ryan.song
 * @Date 2018/11/13
 **/
public abstract class Wma2Pcm {

    public static void wma2PCM(String wma, String pcm){
        try{
            InputStream in = new FileInputStream(wma);
            OutputStream out = new FileOutputStream(pcm);
            byte[] wmaData = inputStream2Byte(in);
            byte[] pcmData = Arrays.copyOfRange(wmaData, 44, wmaData.length);
            out.write(pcmData);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    private static byte[] inputStream2Byte(InputStream in) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ( (len = in.read(buffer)) !=-1){
            byteStream.write(buffer, 0, len);
        }
        byte[] result = byteStream.toByteArray();
        IOUtils.closeQuietly(byteStream);
        return result;
    }

    public static void main(String[] args) {
        wma2PCM("D:\\songjunbao\\work\\softs\\nls-sdk-java-demo\\src\\main\\resources\\sh.wma", "D:\\songjunbao\\work\\softs\\nls-sdk-java-demo\\src\\main\\resources\\sh.pcm");

    }
}
