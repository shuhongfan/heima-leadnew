package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * 消费者
 */
public class ConsumerQuickStart {

    public static void main(String[] args) {

        //1.kafka的配置信息
        Properties prop = new Properties();
        //链接地址
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");
        //key和value的反序列化器
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //设置消费者组
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");

        //手动提交偏移量
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);


        //2.创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop);

        //3.订阅主题
        consumer.subscribe(Collections.singletonList("itcast-topic-out"));

        //4.拉取消息

        //同步提交和异步提交偏移量
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    System.out.println(consumerRecord.key());
                    System.out.println(consumerRecord.value());
                    System.out.println(consumerRecord.offset());
                    System.out.println(consumerRecord.partition());
                }
                //异步提交偏移量
                consumer.commitAsync();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("记录错误的信息："+e);
        }finally {
            //同步
            consumer.commitSync();
        }


        /*while (true){
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                System.out.println(consumerRecord.key());
                System.out.println(consumerRecord.value());
                System.out.println(consumerRecord.offset());
                System.out.println(consumerRecord.partition());

               *//* try {
                    //同步提交偏移量
                    consumer.commitSync();
                }catch (CommitFailedException e){
                    System.out.println("记录提交失败的异常："+e);
                }*//*
            }
            //异步的方式提交偏移量
            *//*consumer.commitAsync(new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                    if(e != null){
                        System.out.println("记录错误的提交偏移量："+map+",异常信息为："+e);
                    }
                }
            });*//*



        }*/

    }

}
