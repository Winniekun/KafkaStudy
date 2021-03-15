package com.wkk.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * @author weikunkun
 * @since 2021/3/15
 */
public class ConsumerSample {
    private final static String TOPIC_NAME = "wkk_topic";

    private static void helloWorld() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "47.95.196.129:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        while (true) {
            // 每1s拉取一回
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partition = % d, offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), record.value());
        }

    }

    public static void committedOffset() {

    }

    public static void main(String[] args) {
        helloWorld();
    }

}
