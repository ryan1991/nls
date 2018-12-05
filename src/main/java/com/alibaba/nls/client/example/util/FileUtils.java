package com.alibaba.nls.client.example.util;

import java.io.*;

/**
 * File 读取和写入操作
 */
public class FileUtils {

    /**
     * 以字节为单位读取文件内容，一次读一个字节：
     * @param fileName
     * @return
     */
    public static InputStream readFile(String fileName) {
        File file = new File(fileName);
        InputStream in = null;
        try {
            // 一次读一个字节
            in = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
           /* try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        return in;
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     * @param fileName
     */
    public static void readFileByBytes(String fileName) {
        File file = new File(fileName);
        InputStream in = null;
        try {
            System.out.println("-------------------以字节为单位读取文件内容，一次读一个字节：------------------");
            // 一次读一个字节
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                System.out.write(tempbyte);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            System.out.println("-------------------以字节为单位读取文件内容，一次读多个字节：-----------------");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            FileUtils.showAvailableBytes(in);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     * @param fileName
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("------------------以字符为单位读取文件内容，一次读一个字节：-------------------");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file),"utf-8");
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("-------------以字符为单位读取文件内容，一次读多个字节：----------------");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName),"utf-8");
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     * @param fileName
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader bufferedReader = null;
        Reader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            //reader = new BufferedReader(new FileReader(file));
            reader = new InputStreamReader(new FileInputStream(fileName), "utf-8");
            bufferedReader = new BufferedReader(reader);
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = bufferedReader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    public static String convertFileToString(String fileName){
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null){
                String line = temp.trim();
                if (line.length() > 0){
                    sb.append(line + "\n");
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }



    /**
     * 随机读取文件内容
     * @param fileName
     */
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 显示输入流中还剩的字节数
     * @param in
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A方法追加文件：使用RandomAccessFile
     * @param fileName 文件名
     * @param content 内容
     */
    public static void appendMethodA(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * B方法追加文件：使用FileWriter
     * @param fileName 文件名
     * @param content 内容
     */
    public static void appendMethodB(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改文件编码格式
     * @param oldFile
     * @param newFile
     */
    public static void gbToUtf(String oldFile,String newFile){
        DataInputStream daStream = null;
        FileInputStream fInputStream = null;
        BufferedReader buReader = null;
        Writer writer = null;
        try {
            String line_separator = System.getProperty("line.separator");
            fInputStream = new FileInputStream(oldFile);
            StringBuffer content = new StringBuffer();
            daStream = new DataInputStream(fInputStream);
            buReader = new BufferedReader(new InputStreamReader(daStream, "GBK"));
            String line = null;
            while ((line = buReader.readLine()) != null)
                content.append(line + line_separator);
            File file = new File(newFile);
            if (file.exists()) {
                file.delete();
                System.out.println("删除成功");
            }
            writer = new OutputStreamWriter(new FileOutputStream(newFile), "utf-8");
            writer.write(content.toString());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                buReader.close();
                daStream.close();
                fInputStream.close();
                writer.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }


    /**
     * 修改文件编码格式
     * @param oldFile
     * @param newFile
     */
    public static void gbChangeUtf(String oldFile,String newFile){
        FileInputStream fInputStream = null;
        BufferedReader buReader = null;
        Writer writer = null;
        try {
            String line_separator = System.getProperty("line.separator");
            StringBuffer content = new StringBuffer();
            fInputStream = new FileInputStream(oldFile);
            buReader = new BufferedReader(new InputStreamReader(fInputStream, "GBK"));
            String line = null;
            while ((line = buReader.readLine()) != null)
                content.append(line + line_separator);
            File file = new File(newFile);
            if (file.exists()) {
                file.delete();
                System.out.println("删除成功");
            }
            writer = new OutputStreamWriter(new FileOutputStream(newFile), "utf-8");
            writer.write(content.toString());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                buReader.close();
                fInputStream.close();
                writer.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) {
        String content = convertFileToString("D:\\songjunbao\\work\\code\\nls\\src\\main\\resources\\log.txt");
        System.out.println("--content:"+content);
    }
}
