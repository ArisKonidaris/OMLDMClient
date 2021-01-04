package omlClient.kafkaCommunication.consumers;

import omlClient.kafkaCommunication.KafkaConstants;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

/**
 * A class, wrapping a Kafka consumer.
 */
public class OMLDMConsumer {

    // The actual Kafka consumer.
    Consumer<String, String> consumer;

    public OMLDMConsumer() {

    }

    // Main constructor
    public OMLDMConsumer(String topic, String brokerList, String groupId) {

        // Creating the properties of the Response Kafka consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        System.out.println("INFO [KafkaConsumer]: " + this.toString() + " subscribed to topic " + topic);
    }

    /**
     * This method consumes records from the Kafka topic.
     *
     * @return A set of records.
     */
    public ConsumerRecords<String, String> consumeRecords() {
        return consumer.poll(100);
    }

    /**
     * This method closes the consumer if its not null.
     * Never forget to call this method after you are done
     * this response consumer.
     */
    public void close() {
        if (consumer != null) {
            System.out.println("Terminating the Kafka responses consumer " + this.toString() + ".");
            consumer.close();
        }
    }

    /**
     * A factory method
     *
     * @param topic       The name of this topic.
     * @param brokerList The Kafka broker's address. If Kafka is running in a cluster
     *                    then you can provide comma (,) separated addresses.
     * @return A ResponseConsumer instance
     */
    public static OMLDMConsumer setConsumer(String topic, String brokerList, String groupId) {
        return new OMLDMConsumer(topic, brokerList, groupId);
    }

}
