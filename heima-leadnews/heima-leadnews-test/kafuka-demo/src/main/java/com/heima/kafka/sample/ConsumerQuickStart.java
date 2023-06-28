package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConsumerQuickStart {
    public static void main(String[] args) {
        //1.添加kafka的配置信息
        Properties properties = new Properties();

        //kafka的连接地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");

        //消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");

        //手动提交偏移量
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        //消息的反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //2.创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        //3.订阅主题
        consumer.subscribe(Collections.singletonList("topic-first"));

        //4.拉取消息

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    System.out.println(consumerRecord.key());
                    System.out.println(consumerRecord.value());
                    System.out.println(consumerRecord.offset());
                    System.out.println(consumerRecord.partition());
                }
                consumer.commitAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("记录错误的信息："+e);
        }finally {
            consumer.commitSync();
        }


        /**
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                System.out.println(consumerRecord.key());
                System.out.println(consumerRecord.value());
                System.out.println(consumerRecord.offset());
                System.out.println(consumerRecord.partition());

//                同步提交偏移量
//                try {
//                    consumer.commitSync();
//                } catch (CommitFailedException e) {
//                    System.out.println("记录提交失败的异常："+e);
//                }
            }

//            异步方式提交偏移量
//            consumer.commitAsync(new OffsetCommitCallback() {
//                @Override
//                public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
//                    if (e != null) {
//                        System.out.println("记录错误的提交偏移量：" + map + ",异常信息为：" + e);
//                    }
//                }
//            });

        }
         **/
    }
}
