package com.wkk.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.checkerframework.checker.units.qual.K;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author weikunkun
 * @since 2021/5/1
 */
@Slf4j
public class KafkaProducerAnalysisTest {
    static final String TOPIC = "wkk-demo";

    /**
     * 最简单的方式
     * 相比而言，性能最佳，不过可靠性最差
     */
    @Test
    public void sendAndForget() {
        Properties properties = KafkaProducerAnalysis.initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "this is send then forget model");
        try {
            producer.send(record);
        } catch (Exception e) {
            log.info("error happened: ", e);
        }
        producer.close();

    }

    /**
     * 同步发送
     * 发送方式可靠，要么发送成功，要么发送异常
     * 不过性能较差，需要因为没发送一条数据都会进行阻塞，知道消息获取成功之后，才能继续发送
     */
    @Test
    public void sync() {
        Properties properties = KafkaProducerAnalysis.initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        try {
            log.info("msg start send " + System.currentTimeMillis());
            for (int i = 0; i < 100; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "this is sync send model " + i);
                RecordMetadata metadata = producer.send(record).get();
                log.info("msg already send success, topic: {}, partition: {}, time: {}", metadata.topic(),
                        metadata.partition(), metadata.timestamp());
            }
        } catch (Exception e) {
            log.info("error happened: ", e);
        }
        producer.close();
    }

    /**
     * 异步发送方式
     * 在send方法中，使用回调函数来处理
     * 一一对应，使用send返回future对象，对于发送较多的对象，不方便处理
     */
    @Test
    public void async() {
        Properties properties = KafkaProducerAnalysis.initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "this is sync send model");
        try {
            log.info("msg start send " + System.currentTimeMillis());
            producer.send(record, new Callback() {
                /**
                 * 共分为两种情况
                 * 1. e != null
                 *  说明了出现了问题
                 * 2. e == null
                 * 数据被正常发送了
                 * @param metadata
                 * @param e
                 */
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (e != null) {
                        log.info("error happened: " + e);
                    } else {
                        log.info("msg already send success, topic: {}, partition: {}, time: {}", metadata.topic(),
                                metadata.partition(), metadata.timestamp());
                    }
                }
            });
        } catch (Exception e) {
            log.info("error happened: ", e);
        }
        producer.close();
    }
}