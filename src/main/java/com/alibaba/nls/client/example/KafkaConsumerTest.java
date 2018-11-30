package com.alibaba.nls.client.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.alibaba.nls.client.example.demo.AudioUtils;
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

	private KafkaConsumer<String, byte[]> consumer;
	private ConsumerRecords<String, byte[]> msgList;
	private  String topic;
	private static final String GROUPID = "groupE4";

	
	public KafkaConsumerTest(String topicName) {
		this.topic = topicName;
		init();
	}
	
	@Override
	public void run() {
		System.out.println("---------开始消费---------");
		List<String> list=new ArrayList<String>();
		List<Long> list2=new ArrayList<Long>();
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

						String appKey = SpeechProperties.APPKEY;
						String token = SpeechProperties.TOKEN;

						SpeechTranscriberWithMicrophoneDemo demo = new SpeechTranscriberWithMicrophoneDemo(appKey, token, "D:\\songjunbao\\work\\code\\nls\\src\\main\\resources\\log.txt");
						demo.process(record.value());
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
		Properties props = new Properties();
		//kafka消费的的地址
		props.put("bootstrap.servers",  "172.16.34.34:9092");
		//组名 不同组名可以重复消费
		props.put("group.id", GROUPID);
		//是否自动提交
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		//超时时间
		props.put("session.timeout.ms", "30000");
		//一次最大拉取的条数
		props.put("max.poll.records", 10);
//		earliest当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
//		latest
//		当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
//		none
//		topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
		props.put("auto.offset.reset", "earliest");

		//序列化
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", ByteArrayDeserializer.class.getName());
		this.consumer = new KafkaConsumer<String, byte[]>(props);
		//订阅主题列表topic
		this.consumer.subscribe(Arrays.asList(topic));
		
		System.out.println("初始化!");
	}
	
	
   
	public static void main(String args[]) {
		KafkaConsumerTest test1 = new KafkaConsumerTest("KAFKA_TEST2");
		Thread thread1 = new Thread(test1);
		thread1.start();
	}
}
