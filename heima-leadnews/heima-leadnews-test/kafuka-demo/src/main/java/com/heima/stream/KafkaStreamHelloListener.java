package com.heima.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@Slf4j
public class KafkaStreamHelloListener {

    @Bean
    public KStream<String, String> KStream(StreamsBuilder streamsBuilder) {
//        创建kStream对象，同时指定从哪个topic中接收消息
        KStream<String, String> stream = streamsBuilder.stream("itcast-topic-input");
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
                .to("itcast-topic-out");

        return stream;
    }

}
