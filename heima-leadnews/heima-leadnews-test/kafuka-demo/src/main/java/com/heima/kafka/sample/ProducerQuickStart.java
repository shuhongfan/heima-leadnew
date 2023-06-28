package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerQuickStart {
    public static void main(String[] args) {
//        1.kafka链接配置信息
        Properties properties = new Properties();

//          kafka的连接地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");

//          发送失败，失败的重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,5);

        //消息key的序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //消息value的序列化器
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

//        ack配置 消息确认机制
        properties.put(ProducerConfig.ACKS_CONFIG, "all");

//        重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG, 10);

//        数据压缩
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

//        2.创建kafka生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

//        3.发送消息
        ProducerRecord<String, String> record = new ProducerRecord<>("topic-first", "key-001", "hello kafka");
        producer.send(record);

//        4.关闭消息通道
        producer.close();
    }
}
