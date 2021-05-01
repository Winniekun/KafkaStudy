package com.wkk.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author weikunkun
 * @since 2021/5/1
 */
@Slf4j
public class KafkaProducerAnalysis {
    public static final String brokerList = "47.95.224.162:9092";
    public static final String topic = "topic-demo";

    public static Properties initConfig() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DemoPartitioner.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerInterceptorPrefix.class.getName());
        // 类似于线程池中线程的名称，自定义，默认为producer-1...
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer.client.id.demo");
        return props;
    }

    /**
     * 发后即忘模式
     */
    public static void sendAndForget() {
        Properties properties = initConfig();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "hello this is send " +
                "then forget model");
        try (
                KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);
        ) {
            kafkaProducer.send(record);
        } catch (Exception e) {
            log.info("error: ", e);
        }
    }

    /**
     * 同步方式
     */
    public static void sync() {
        Properties properties = initConfig();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "hello this is send " +
                "then forget model");
        try (
                KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);
        ) {
            kafkaProducer.send(record);
        } catch (Exception e) {
            log.info("error: ", e);
        }
    }

    /**
     * 异步方式
     */
    public static void async() {

    }

    public static void main(String[] args) {

    }
}
