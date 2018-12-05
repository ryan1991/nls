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

import com.alibaba.nls.client.example.demo.AudioUtils;
import com.alibaba.nls.client.example.util.FileUtils;
import com.alibaba.nls.client.example.util.KafkaPropertiesFactory;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.Arrays;

/**
 * SpeechTranscriberWithMicrophoneDemo class
 *
 * 使用麦克风音频流的实时音频流识别Demo
 * @author siwei
 * @date 2018/6/25
 */
public class SpeechTranscriberOnline {
    private String appKey;
    private String accessToken;
    private String filePath;
    NlsClient client;

    private KafkaConsumer<String, byte[]> consumer;
    private ConsumerRecords<String, byte[]> msgList;
    private  String topic;

    public SpeechTranscriberOnline(String appKey, String token, String filePath,String topicName) {
        this.appKey = appKey;
        this.accessToken = token;
        this.filePath = filePath;
        this.topic = topicName;
        init();
        // Step0 创建NlsClient实例,应用全局创建一个即可,默认服务地址为阿里云线上服务地址
        client = new NlsClient(accessToken);
    }

    private void init() {
        this.consumer = new KafkaConsumer<String, byte[]>(KafkaPropertiesFactory.newConsumerProperties());
        //订阅主题列表topic
        this.consumer.subscribe(Arrays.asList(topic));

        System.out.println("初始化!");
    }

    public SpeechTranscriberListener getTranscriberListener() {
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
                FileUtils.appendMethodB(filePath,response.getTransSentenceText()+"\n");
            }
            // 识别完毕
            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                System.out.println("name: " + response.getName() +
                        ", status: " + response.getStatus() +
                        // 句子编号，从1开始递增
                        ", index: " + response.getTransSentenceIndex() +
                        // 当前句子的完整识别结果
                        ", result: " + response.getTransSentenceText() +
                        // 当前已处理的音频时长，单位是毫秒
                        ", time: " + response.getTransSentenceTime());
            }
        };

        return listener;
    }

    public void process() {
        SpeechTranscriber transcriber = null;
        try {
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
            transcriber.setEnableITN(true);

            // Step2 此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
            transcriber.start();

            while (true) {
                //主动从kafka中拉数据
                msgList = consumer.poll(100);
                if(msgList.count() <= 0){
                    System.out.println("未接受到数据！");
                    continue;
                }
                for (ConsumerRecord<String, byte[]> record : msgList) {
                    System.out.println("kafka 接收消息：" +record.offset());
                    transcriber.send(record.value());
                    // 客户端发送数据过快，服务器资源已经耗尽[41040202]
                    Thread.sleep(100);
//                    try {
//                        SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();
//                        sourceDataLine.write(record.value(),0, record.value().length);
//
//                    } catch (LineUnavailableException e) {
//                        e.printStackTrace();
//                    }
                }
            }
            // Step5 通知服务端语音数据发送完毕,等待服务端处理完成
//            transcriber.stop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            // Step6 关闭连接
            if (null != transcriber) {
                transcriber.close();
            }
            if (consumer != null){
                consumer.close();
            }
        }
    }

    public void shutdown() {
        client.shutdown();
    }

    public static void main(String[] args) {
        SpeechTranscriberOnline demo = new SpeechTranscriberOnline(SpeechProperties.APPKEY, SpeechProperties.TOKEN , "D:\\songjunbao\\work\\code\\nls\\src\\main\\resources\\log.txt","voice-topic");
        demo.process();
        demo.shutdown();
    }
}
