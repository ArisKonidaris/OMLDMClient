package omlClient.kafkaCommunication.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import omlClient.kafkaCommunication.KafkaConstants;
import ControlAPI.Request;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * A class, wrapping a Kafka request producer. The class provides several
 * helper methods for sending requests and managing the Kafka Producer.
 */
public class RequestProducer {

    private String topic; // The topic name for the requests
    private Integer partitions; // The number of partitions this topic
    private Producer<Long, String> producer; // The actual Kafka producer
    private long counter; // A counter utilized as a key for the Kafka messages into the POJO Request class

    // A default, empty constructor
    public RequestProducer() {
    }

    // Main constructor
    public RequestProducer(String topic, String broker_list, Integer partitions) {

        this.topic = topic;
        this.partitions = partitions;
        this.counter = 0L;

        // Creating the properties of the Json Requests Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker_list);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstants.REQUEST_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        System.out.println("INFO [KafkaRequestProducer]: " + this.toString());

    }

    /**
     * This method writes a String request written in JSON format into the requests topic.
     *
     * @param request A String request in JSON format.
     * @param verbose A flag for printing the request that was sent.
     */
    private void sendRequest(String request, boolean verbose) {
        try {
            producer.send(new ProducerRecord<>(topic, ((int) counter) % this.partitions, counter, request));
            if (verbose)
                System.out.println("INFO [KafkaRequestProducer]:  runProducer(), counter=" +
                        counter + ", request: " +
                        request.replaceAll("[\\s\r\n]+", ""));
            if (counter == Long.MAX_VALUE) counter = 0L;
            else counter++;
        } catch (Exception e) {
            System.out.println("Couldn't write request " + request + " to the Kafka topic " + topic + ".");
        }
    }

    /**
     * This method writes a request, provided as a String and
     * written in JSON format, into the requests topic
     *
     * @param req     A String request in JSON format.
     * @param verbose A flag for printing the request that was sent.
     */
    public void sendJsonStringAsRequest(String req, boolean verbose) throws JsonProcessingException {
        Request request = new ObjectMapper().readValue(req, Request.class);
        if (request.isValid()) sendRequest(request.toString(), verbose);
    }

    /**
     * This method writes the requests, written into
     * the provided file, into the requests topic.
     *
     * @param filepath The absolute path of the file with the JSON requests.
     * @param verbose  A flag for printing the request that was sent.
     */
    public void sendRequestsFromFile(String filepath, boolean verbose) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filepath));
        if (!root.isArray()) {
            throw new RuntimeException("A JSON Array is expected");
        } else {
            System.out.println("INFO [KafkaRequestProducer]:  runProducer(), read file");
            for (Request request : mapper.convertValue(root, Request[].class))
                if (request.isValid()) sendRequest(request.toString(), verbose);
        }
    }

    /**
     * This method writes the requests, written into
     * the provided file, into the requests topic.
     *
     * @param filepath The absolute path of the file with the JSON requests.
     */
    public void sendRequestsFromFile(String filepath) throws IOException {
        sendRequestsFromFile(filepath, false);
    }

    /**
     * This method closes the producer if its not null.
     * Never forget to call this method after you are done
     * this request producer.
     */
    public void close() {
        if (producer != null) {
            System.out.println("Terminating the Kafka producer " + this.toString() + ".");
            producer.close();
        }
    }

    /**
     * A factory method
     *
     * @param topic       The name of this topic.
     * @param partitions  The number of partitions this topic.
     * @param broker_list The Kafka broker's address. If Kafka is running in a cluster
     *                    then you can provide comma (,) separated addresses.
     * @return A RequestProducer instance.
     */
    public RequestProducer setProducer(String topic, String broker_list, Integer partitions) {
        return new RequestProducer(topic, broker_list, partitions);
    }
}
