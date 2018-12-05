package com.alibaba.nls.client.example.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @Author ryan.song
 * @Date 2018/12/4
 **/
public class KafkaPropertiesFactory {

    private KafkaPropertiesFactory(){}


    public static Properties newProduceProperties(){
        Properties props = new Properties();

        props.put("bootstrap.servers",  "172.16.34.53:9092");
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

        return props;
    }


    public static Properties newConsumerProperties(){
        Properties props = new Properties();
        //kafka服务地址
        props.put("bootstrap.servers",  "172.16.34.53:9092");
        //组名 不同组名可以重复消费
        props.put("group.id", "groupE4");
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
        return props;
    }

}
