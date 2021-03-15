package com.wkk.kafka.wechat.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author weikunkun
 * @since 2021/3/15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String acksConfig;
    private String partitionerClass;
}
