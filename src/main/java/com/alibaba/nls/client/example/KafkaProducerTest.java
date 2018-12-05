package com.alibaba.nls.client.example;

import java.util.Properties;

import com.alibaba.nls.client.example.demo.AudioUtils;
import com.alibaba.nls.client.example.util.KafkaPropertiesFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;


/**
 * 
* Title: KafkaProducerTest
* Description: 
* kafka 生产者demo
* Version:1.0.0  
* @author pancm
* @date 2018年1月26日
 */
public class KafkaProducerTest implements Runnable {

	private final KafkaProducer<String, byte[]> producer;
	private final String topic;

	
	public KafkaProducerTest(String topicName) {
		this.producer = new KafkaProducer<String, byte[]>(KafkaPropertiesFactory.newProduceProperties());
		this.topic = topicName;
	}

	@Override
	public void run() {
		try {
				// Step3 读取麦克风数据
//			AudioFormat audioFormat = new AudioFormat(16000.0F, 16, 1, true, false);
//			DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
//			TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
//			targetDataLine.open(audioFormat);
//			targetDataLine.start();
			TargetDataLine targetDataLine = AudioUtils.getTargetDataLine();
			System.out.println("You can speak now!");
			int nByte = 0;
			final int bufSize = 3200;
			byte[] buffer = new byte[bufSize];
			while ((nByte = targetDataLine.read(buffer, 0, bufSize)) > 0) {
				// Step4 直接发送麦克风数据流
				producer.send(new ProducerRecord<String, byte[]>(topic, "Message", buffer));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			producer.close();
		}
	}
	
	public static void main(String args[]) {
		KafkaProducerTest test = new KafkaProducerTest("voice-topic");
		Thread thread = new Thread(test);
		thread.start();
	}
	

}