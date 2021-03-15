package com.wkk.kafka.producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * 自定义分区器
 *
 * @author weikunkun
 * @since 2021/3/15
 */
public class PartitionSample implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        /**
         * key 格式
         * key-1
         * key-2
         * key-3
         * ...
         */
        String keyStr = key + "";
        String keyIntSub = keyStr.substring(4);
        System.out.println("keyStr: " + keyStr + ", keyIntSub: " + keyIntSub);
        int number = Integer.parseInt(keyIntSub);
        return number % 2;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
