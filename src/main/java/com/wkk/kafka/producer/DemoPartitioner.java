package com.wkk.kafka.producer;

import org.apache.kafka.clients.MetadataUpdater;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weikunkun
 * @since 2021/5/1
 */
public class DemoPartitioner implements Partitioner {
    private final AtomicInteger counter = new AtomicInteger(0);
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // 先获取该topic下有多少分区
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        int numPartitions = partitionInfos.size();
        if (null == keyBytes) { // key 为空
            int curNumber = counter.getAndIncrement();
            return  curNumber % numPartitions;
        } else { // 不为空
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
