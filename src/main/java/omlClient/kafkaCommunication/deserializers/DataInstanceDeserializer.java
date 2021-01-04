package omlClient.kafkaCommunication.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.DataInstance;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * A Kafka Deserializer class for deserializing the data points
 * in the Online Machine Leaning component. This class is not
 * actually used in the client. It is used inside the Online
 * Machine Learning component in Flink.
 */
public class DataInstanceDeserializer implements Deserializer<DataInstance> {

    public DataInstance deserializeDataInstance(byte[] bytes) {
        DataInstance dataPoint = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dataPoint = objectMapper.readValue(bytes, DataInstance.class);
        } catch (Exception e) {
            System.out.println("Error in deserializing data point.");
        }
        return dataPoint;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public DataInstance deserialize(String s, byte[] bytes) {
        return deserializeDataInstance(bytes);
    }

    @Override
    public DataInstance deserialize(String topic, Headers headers, byte[] data) {
        return deserializeDataInstance(data);
    }

    @Override
    public void close() {

    }
}
