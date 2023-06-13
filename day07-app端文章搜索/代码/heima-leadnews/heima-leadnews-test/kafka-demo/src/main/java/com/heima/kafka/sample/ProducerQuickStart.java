package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 生产者
 */
public class ProducerQuickStart {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //1.kafka链接配置信息
        Properties prop = new Properties();
        //kafka链接地址
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");
        //key和value的序列化
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        //ack配置  消息确认机制
        prop.put(ProducerConfig.ACKS_CONFIG,"all");

        //重试次数
        prop.put(ProducerConfig.RETRIES_CONFIG,10);

        //数据压缩
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"lz4");

        //2.创建kafka生产者对象
        KafkaProducer<String,String> producer = new KafkaProducer<String,String>(prop);

        //3.发送消息
        /**
         * 第一个参数 ：topic
         * 第二个参数：消息的key
         * 第三个参数：消息的value
         */
        ProducerRecord<String,String> kvProducerRecord = new ProducerRecord<String,String>("topic-first","hello kafka");
        //同步发送消息
        /*RecordMetadata recordMetadata = producer.send(kvProducerRecord).get();
        System.out.println(recordMetadata.offset());*/

        //异步消息发送
        producer.send(kvProducerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if(e != null){
                    System.out.println("记录异常信息到日志表中");
                }
                System.out.println(recordMetadata.offset());
            }
        });

        //4.关闭消息通道  必须要关闭，否则消息发送不成功
        producer.close();



    }

}
