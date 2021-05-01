## Producer 
Producer（生产者）顾名思义就是用于用于生产消息的主体，其主要步骤如下
1. 配置客户端参数以及创建相应的生产者实例
2. 构建待发送的消息
3. 发送消息
4. 关闭生产者实例

## 生产者的配置
```java
public static Properties initConfig() {
    Properties props = new Properties();
    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    // 类似于线程池中线程的名称，自定义，默认为producer-1...
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer.client.id.demo");
    return props;
}
```
## 消息的构造&发送

创建生产者实例，并且消息构建好了之后，就可以发送消息了，对于消息的发送，主要有三种模式：

1. 发后即忘

   ```java
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
   ```

   

2. 同步

   ```java
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
           for (int i = 0; i < 10; i++) {
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
   ```

   

3. 异步

   ```java
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
   ```

   

## 消息的序列化

可进行自定义序列化，实现Serializer\<T\>接口

## 自定义分区

消息通过send()方法发送到broker中，期间可能会经过系列的过程，拦截器、序列化、分区器等一系列作用。

消息经过序列化之后，就需要考虑发送到哪个分区了

1. 如果ProducerRecord中指定了对应的分区，则直接吸入该分区内
2. 如果ProducerRecord中未指定对应的分区，则需要依赖分区器，根据key来计算partition的值
   1. 如果key不为null，使用MurmurHash2算法，根据最终的hash值来计算分区号
   2. 如果key为null，消息将会以轮询的方式发往主题内的各个可用分区

## 生产者拦截

拦截器的主要作用

1. 可以在消息放松前做一些准备工作，比如按照某个规则过滤不符合要求的消息、修改消息的内容等

2. 在发送回调逻辑前做一些定制话的需求，比如统计类的工作

   ```java
   /**
    * 生产者拦截器
    * @author weikunkun
    * @since 2021/5/1
    */
   @Slf4j
   public class ProducerInterceptorPrefix implements ProducerInterceptor<String, String> {
       private volatile long sendSuccess = 0;
       private volatile long sendFailure = 0;
       @Override
       public ProducerRecord<String, String> onSend(ProducerRecord<String, String> producerRecord) {
           String modifyValue = "prefix1-" + producerRecord.value();
           return new ProducerRecord<>(producerRecord.topic(), producerRecord.partition(),
                   producerRecord.timestamp(), producerRecord.key(), modifyValue, producerRecord.headers());
       }
   
       @Override
       public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
           if (e == null) { // 消息正常
               sendSuccess++;
           } else {
               sendFailure++;
           }
       }
   
       @Override
       public void close() {
           double successRatio = (double) sendSuccess / (sendFailure + sendSuccess) * 100;
           log.info("[INFO] msg send success rate: {}%", successRatio);
       }
   
       @Override
       public void configure(Map<String, ?> map) {
   
       }
   }
   ```

   

## 生产者整体架构

![image-20210501232638092](https://i.loli.net/2021/05/01/CXGO2R5QftUAkLT.png)


## 一些重要的生产者配置

1. acks

   用于指定分区中必须有多少副本收到这条消息，之后生产者才认为消息是写入成功的，其涉及到消息的可靠传输和吞吐量之间的权衡

   ```java
   properties.put(ProducerConfig.ACKS_CONFIG, "1");
   properties.put(ProducerConfig.ACKS_CONFIG, "0");
   properties.put(ProducerConfig.ACKS_CONFIG, "all");
   ```

   * Ack = 1

     折中方式

   * Ack = 0

     达到最大吞吐量，不过消息丢失无法处理

   * ack = all

     所有副本都成功写入消息，才能接收到服务端的成功响应。最强的可靠性，当然如果只有leader副本，则也就退化到了ack=1

   