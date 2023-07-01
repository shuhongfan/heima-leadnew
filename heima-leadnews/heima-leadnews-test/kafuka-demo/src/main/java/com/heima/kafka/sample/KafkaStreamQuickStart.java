package com.heima.kafka.sample;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamQuickStart {
    public static void main(String[] args) {
        //kafka的配置信息
        Properties prop = new Properties();
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams-quickstart");

//        stream构建器
        StreamsBuilder streamsBuilder = new StreamsBuilder();

//        流式计算
        streamProcessor(streamsBuilder);

//        创建kafukaStrem对象
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), prop);

//        开启流式计算
        kafkaStreams.start();
    }

    /**
     * 流式计算
     * 消息的内容 Hello kafka
     * @param streamsBuilder
     */
    private static void streamProcessor(StreamsBuilder streamsBuilder) {
//        创建kStream对象，同时指定从哪个topic中接受消息
        KStream<String, String> stream = streamsBuilder.stream("itcast-topic-input");

//        处理消息的value
        stream.flatMapValues(new ValueMapper<String, Iterable<?>>() {
                    @Override
                    public Iterable<?> apply(String value) {
                        String[] valArr = value.split(" ");
                        return Arrays.asList(valArr);
                    }
                })
                .groupBy((key, value) -> value)  //按照value进行聚合处理
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10))) // 时间窗口
                .count() // 统计单词的个数
                .toStream() //转换为kstream对象
                .map((key,value)->{
                    System.out.println("key:" + key + ",value:" + value);
                    return new KeyValue<>(key.key().toString(), value.toString());  //转换为字符串
                })
                .to("itcast-topic-out"); //发送消息

    }
}
