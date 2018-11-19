/*
 * Copyright 2015 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nls.client.example;

import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;

/**
 * SpeechTranscriberDemo class
 *
 * 实时音频流识别Demo
 * @author siwei
 * @date 2018/6/25
 */
public class SpeechTranscriberDemo {
    private String appKey;
    private String accessToken;
    NlsClient client;
    ServerSocket serverSocket;
    OutputStream out ;
    static InputStream in;

    public SpeechTranscriberDemo(String appKey, String token) {
        this.appKey = appKey;
        this.accessToken = token;
        // Step0 创建NlsClient实例,应用全局创建一个即可,默认服务地址为阿里云线上服务地址
        client = new NlsClient(accessToken);

    }

    private  SpeechTranscriberListener getTranscriberListener() {
        SpeechTranscriberListener listener = new SpeechTranscriberListener() {
            // 识别出中间结果.服务端识别出一个字或词时会返回此消息.仅当setEnableIntermediateResult(true)时,才会有此类消息返回
            @Override
            public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                System.out.println("name: " + response.getName() +
                        // 状态码 20000000 表示正常识别
                        ", status: " + response.getStatus() +
                        // 句子编号，从1开始递增
                        ", index: " + response.getTransSentenceIndex() +
                        // 当前句子的中间识别结果
                        ", result: " + response.getTransSentenceText() +
                        // 当前已处理的音频时长，单位是毫秒
                        ", time: " + response.getTransSentenceTime());
            }
            // 识别出一句话.服务端会智能断句,当识别到一句话结束时会返回此消息
            @Override
            public void onSentenceEnd(SpeechTranscriberResponse response) {
                System.out.println("name: " + response.getName() +
                        // 状态码 20000000 表示正常识别
                        ", status: " + response.getStatus() +
                        // 句子编号，从1开始递增
                        ", index: " + response.getTransSentenceIndex() +
                        // 当前句子的完整识别结果
                        ", result: " + response.getTransSentenceText() +
                        // 当前已处理的音频时长，单位是毫秒
                        ", time: " + response.getTransSentenceTime());
                try {
                    out.write(response.getTransSentenceText().getBytes());
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
            // 识别完毕
            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                System.out.println("name: " + response.getName() +
                        ", status: " + response.getStatus());
            }
        };

        return listener;
    }

    public void process() {



        SpeechTranscriber transcriber = null;
        try {
                System.out.println("---server 创建开始");
                serverSocket = new ServerSocket(9000, 20);
                System.out.println("---server 创建成功");
                out = serverSocket.accept().getOutputStream();
                in = serverSocket.accept().getInputStream();


            // Step1 创建实例,建立连接
            transcriber = new SpeechTranscriber(client, getTranscriberListener());
            transcriber.setAppKey(appKey);
            // 输入音频编码方式
            transcriber.setFormat(InputFormatEnum.PCM);
            // 输入音频采样率
            transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            // 是否返回中间识别结果
            transcriber.setEnableIntermediateResult(false);
            // 是否生成并返回标点符号
            transcriber.setEnablePunctuation(true);
            // 是否将返回结果规整化,比如将一百返回为100
            transcriber.setEnableITN(false);

            // Step2 此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
            transcriber.start();
            // Step3 语音数据来自声音文件用此方法,控制发送速率;若语音来自实时录音,不需控制发送速率直接调用 recognizer.sent(ins)即可
            while(true){

                transcriber.send(in);
            }
            // Step4 通知服务端语音数据发送完毕,等待服务端处理完成
            //transcriber.stop();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            // Step5 关闭连接
            if (null != transcriber) {
                transcriber.close();
            }
        }
    }

    public void shutdown() {
        client.shutdown();
    }

    public static void main(String[] args) {

        String appKey = SpeechProperties.APPKEY;
        String token = SpeechProperties.TOKEN;

        SpeechTranscriberDemo demo = new SpeechTranscriberDemo(appKey, token);
//        InputStream ins = SpeechTranscriberDemo.class.getResourceAsStream("/hello.wav");
//        if (null == in) {
//            System.err.println("open the audio file failed!");
//            return;
//        }
            demo.process();
//        demo.shutdown();
    }
}
