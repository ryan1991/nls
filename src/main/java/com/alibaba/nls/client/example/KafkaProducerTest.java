package com.alibaba.nls.client.example;

import java.util.Properties;

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
		Properties props = new Properties();
		props.put("bootstrap.servers",  "172.16.34.34:9092");
		//acks=0：如果设置为0，生产者不会等待kafka的响应。
		//acks=1：这个配置意味着kafka会把这条消息写到本地日志文件中，但是不会等待集群中其他机器的成功响应。
		//acks=all：这个配置意味着leader会等待所有的follower同步完成。这个确保消息不会丢失，除非kafka集群中所有机器挂掉。这是最强的可用性保证。
		props.put("acks", "all");
		//配置为大于0的值的话，客户端会在消息发送失败时重新发送。
		props.put("retries", 0);
		//当多条消息需要发送到同一个分区时，生产者会尝试合并网络请求。这会提高client和生产者的效率
		props.put("batch.size", 16384);
        props.put("buffer.memory", 33554432);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", ByteArraySerializer.class.getName());
		this.producer = new KafkaProducer<String, byte[]>(props);
		this.topic = topicName;
	}

	@Override
	public void run() {
		int messageNo = 1;
		try {
				// Step3 读取麦克风数据
                AudioFormat audioFormat = new AudioFormat(16000.0F, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
                TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                targetDataLine.open(audioFormat);
                targetDataLine.start();
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
		KafkaProducerTest test = new KafkaProducerTest("KAFKA_TEST2");
		Thread thread = new Thread(test);
		thread.start();
	}
	

}