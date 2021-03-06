package omlClient.kafkaCommunication.producers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.DataInstance;
import omlClient.kafkaCommunication.KafkaConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A class, wrapping a Kafka data producer for the training data. The class provides
 * several helper methods for sending training data and managing the Kafka Producer.
 */
public class TrainingDataStreamProducer {

    private String topic; // The topic name for the training data.
    private Integer partitions; // The number of partitions this topic.
    private Producer<Long, String> producer; // The actual Kafka producer.
    private long counter; // A counter utilized as a key for the Kafka messages.

    public TrainingDataStreamProducer() {
    }

    // Main constructor.
    public TrainingDataStreamProducer(String topic, String broker_list, Integer partitions) {
        this.topic = topic;
        this.partitions = partitions;
        this.counter = 0L;

        // Creating the properties of the data Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker_list);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstants.DATA_CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        System.out.println("INFO [KafkaDataProducer]: " + this.toString());

    }

    public void sendEndOfStream() {
        for (int p = 0; p < partitions; p++) {
            producer.send(new ProducerRecord<>(topic, p, counter, "EOS"));
        }
    }

    /**
     * This method writes a String data point written in JSON format into the training data topic.
     *
     * @param data    A training data point.
     * @param verbose A flag for printing the training data point that was sent.
     */
    public void sendDataPoint(String data, boolean verbose) {
        try {
            if (this.partitions == 1)
                producer.send(new ProducerRecord<>(topic, counter, data));
            else
                producer.send(new ProducerRecord<>(topic, ((int) counter) % this.partitions, counter, data));
            if (verbose)
                if (counter % 10000 == 0)
                    System.out.println("INFO [KafkaDataProducer]:  runProducer(), counter=" +
                            counter + ", data: " + data);
            if (counter == Long.MAX_VALUE) counter = 0L;
            else counter++;
        } catch (Exception e) {
            System.out.println("Couldn't write the data point " + data + " to the Kafka topic " + topic + ".");
        }
    }

    /**
     * This method writes the training data, written into the provided file in csv format, into the
     * training data topic. This method currently supports only csv files with numerical values only.
     *
     * @param filepath The absolute path of the file with the data set written csv format.
     * @param verbose  A flag for printing the training data point that was sent.
     */
    public void sendDataPointsFromCSVFile(String filepath, boolean verbose) {
//        assert (filepath.endsWith(".csv"));

        List<String> featureNames = Arrays.asList("simulated time", "num cells", "num division", "num death", "wall time", "oxygen", "tnf");

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null && counter < 1000000) {
//            while ((line = br.readLine()) != null) {
                    List<Double> split = Arrays
                        .stream(line.split(","))
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                int size = split.size() - 1;
                DataInstance dataPoint = new DataInstance(split.subList(0, size), split.get(size));
                dataPoint.set("featureNames", featureNames);
                dataPoint.set("run_number", 0);
                dataPoint.set("simulation_name", "spheroid_TNF_nopulse");
                if (dataPoint.isValid())
                    sendDataPoint(dataPoint.toString(), verbose);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong while streaming the file " + filepath + " to Kafka.");
        }
    }

    /**
     * This method writes the training data, written into the
     * provided file in json format, into the training data topic.
     *
     * @param filepath The absolute path of the file with the training data set written json format.
     * @param verbose  A flag for printing the training data point that was sent.
     */
    public void sendDataPointsFromJSONFile(String filepath, boolean verbose) {
        assert (filepath.substring(filepath.length() - 4).equals(".json"));

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(filepath));
            if (!root.isArray()) {
                throw new RuntimeException("A JSON Array is expected");
            } else {
                for (DataInstance dataPoint : mapper.convertValue(root, DataInstance[].class))
                    if (dataPoint.isValid())
                        sendDataPoint(dataPoint.toString(), verbose);
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong while streaming the file " + filepath + " to Kafka.");
        }
    }

    /**
     * This method closes the producer if its not null.
     * Never forget to call this method after you are done
     * this data producer.
     */
    public void close() {
        if (producer != null) {
            System.out.println("Terminating the Kafka producer " + this.toString() + ".");
            producer.close();
        }
    }

    /**
     * A factory method.
     *
     * @param topic       The name of this topic.
     * @param partitions  The number of partitions this topic.
     * @param broker_list The Kafka broker's address. If Kafka is running in a cluster
     *                    then you can provide comma (,) separated addresses.
     * @return A TrainingDataStreamProducer instance.
     */
    public TrainingDataStreamProducer setProducer(String topic, String broker_list, Integer partitions) {
        return new TrainingDataStreamProducer(topic, broker_list, partitions);
    }

}
