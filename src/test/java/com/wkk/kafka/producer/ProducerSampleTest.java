package com.wkk.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * @author weikunkun
 * @since 2021/3/15
 */
public class ProducerSampleTest {
    private static final String TOPIC_NAME = "wkk_topic";
    private Properties properties;

    @Before
    public void genProperties() {
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "47.95.224.162:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // partition
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.wkk.kafka.producer.PartitionSample");
    }

    /**
     * 异步发送
     */
    @Test
    public void testSendMSG() {
        Producer<String, String> producer = new KafkaProducer<>(properties);
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key#" + i, "value-" + i);
            producer.send(record);
        }

        producer.close();

    }

    /**
     * 异步阻塞发送
     *
     * @throws Exception
     */
    @Test
    public void testSendMSGSync() {
        try (Producer<String, String> producer = new KafkaProducer<>(properties);
        ) {
            for (int i = 0; i < 10; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key#" + i, "value---" + i);
                Future<RecordMetadata> send = producer.send(record);
                RecordMetadata recordMetadata = send.get();
                System.out.println("partition: " + recordMetadata.partition() + ", offset: " + recordMetadata.offset());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 异步发送+回调
     */
    @Test
    public void testSendMSGSyncCallback() {
        try (Producer<String, String> producer = new KafkaProducer<>(properties);
        ) {
            for (int i = 0; i < 10; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key#" + i, "value---" + i);
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        System.out.println("回调: " + "partition: " + recordMetadata.partition() + ", offset: " + recordMetadata.offset());
                    }
                });
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 异步回调+partition负载均衡
     */
    @Test
    public void testSendMSGSyncCallbackAndPartition() {
        try (Producer<String, String> producer = new KafkaProducer<>(properties);
        ) {
            for (int i = 0; i < 10; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "key#" + i, "value-" + i);
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        System.out.println("回调: " + "partition: " + recordMetadata.partition() + ", offset: " + recordMetadata.offset());
                    }
                });
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}