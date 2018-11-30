package com.alibaba.nls.client.example;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author ryan.song
 * @Date 2018/11/26
 **/
public class Main {
    public static void main(String[] args)  throws Exception{
        FileWriter fileWriter = new FileWriter("D:\\songjunbao\\work\\code\\nls\\src\\main\\resources\\log.txt");
        fileWriter.write("abc");
        fileWriter.write("efg");
        fileWriter.flush();
        fileWriter.close();

    }

}
