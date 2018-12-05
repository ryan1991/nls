package com.alibaba.nls.client.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.alibaba.nls.client.example.demo.AudioUtils;
import com.alibaba.nls.client.example.util.KafkaPropertiesFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


/**
 * 
* Title: KafkaConsumerTest
* Description: 
* kafka消费者 demo
* 手动提交测试
* Version:1.0.0  
* @author pancm
* @date 2018年1月26日
 */
public class KafkaConsumerTest implements Runnable {

	private KafkaConsumer<String, byte[]>  consumer;
	private ConsumerRecords<String, byte[]> msgList;
	private  String topic;

	
	public KafkaConsumerTest(String topicName) {
		this.topic = topicName;
		init();
	}
	
	@Override
	public void run() {
		System.out.println("---------开始消费---------");
		try {
			for (;;) {
					msgList = consumer.poll(100);
					if(null!=msgList&&msgList.count()>0){
					for (ConsumerRecord<String, byte[]> record : msgList) {

						try {
							SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();
							sourceDataLine.write(record.value(),0, record.value().length);

						} catch (LineUnavailableException e) {
							e.printStackTrace();
						}

//						String appKey = SpeechProperties.APPKEY;
//						String token = SpeechProperties.TOKEN;
//
//						SpeechTranscriberWithMicrophoneDemo demo = new SpeechTranscriberWithMicrophoneDemo(appKey, token, "D:\\songjunbao\\work\\code\\nls\\src\\main\\resources\\log.txt");
//						demo.process();
//						demo.shutdown();

					}
				}else{
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			consumer.close();
		}
	}
	
	private void init() {

		this.consumer = new KafkaConsumer<String, byte[]>(KafkaPropertiesFactory.newConsumerProperties());
		//订阅主题列表topic
		this.consumer.subscribe(Arrays.asList(topic));
		
		System.out.println("初始化!");
	}
	
	
   
	public static void main(String args[]) {
		KafkaConsumerTest test1 = new KafkaConsumerTest("voice-topic");
		Thread thread1 = new Thread(test1);
		thread1.start();
	}
}
