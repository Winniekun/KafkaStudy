package com.wkk.kafka.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author weikunkun
 * @since 2021/3/15
 */
public class AdminSample {
    public final static String TOPIC_NAME = "hanhh_topic";

    public static AdminClient adminClient() {
        Properties props = new Properties();
        props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "47.95.196.129:9092");
        return AdminClient.create(props);
    }

    /**
     * 查看topic list
     */
    public static void listTopics() throws ExecutionException, InterruptedException {
        AdminClient adminClient = adminClient();
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        Set<String> strings = listTopicsResult.names().get();
        strings.stream().forEach(System.out::println);
    }

    /**
     * 创建topic
     */
    public static void createTopic() throws Exception {
        AdminClient client = adminClient();
        Short rs = 1;
        NewTopic newTopic = new NewTopic(TOPIC_NAME, 1, rs);
        client.createTopics(Collections.singletonList(newTopic));
        System.out.println("--list--");
        client.listTopics().names().get().forEach(System.out::println);
    }

    /**
     * 删除topic
     */
    public static void delTopics() throws Exception{
        AdminClient adminClient = adminClient();
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singletonList(TOPIC_NAME));
        deleteTopicsResult.all().get();
    }

    /**
     * 描述信息
     * @throws Exception
     */
    public static void descTopics() throws Exception{
        AdminClient adminClient = adminClient();
        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(TOPIC_NAME));
        Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.all().get();
        Set<Map.Entry<String, TopicDescription>> entries = stringTopicDescriptionMap.entrySet();
        entries.stream().forEach(entry -> {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        });

    }

    public static void descConfig() throws Exception{
        AdminClient adminClient = adminClient();
        ConfigResource.Type type;
        ConfigResource configResource = new ConfigResource(Type.TOPIC, TOPIC_NAME);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Collections.singletonList(configResource));
        Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
        configResourceConfigMap.entrySet().stream().forEach(entry->{
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        });
    }

    public static void main(String[] args) throws Exception {
//        createTopic();
        listTopics();
//        delTopics();
//        descTopics();
//        descTopics();
    }
}
