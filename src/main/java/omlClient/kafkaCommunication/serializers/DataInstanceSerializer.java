package omlClient.kafkaCommunication.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.DataInstance;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * A Kafka Serializer class for serializing a data point to
 * send to the Online Machine Leaning component.
 */
public class DataInstanceSerializer implements Serializer<DataInstance> {

    public byte[] serializeDataInstance(DataInstance data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            System.out.println("Error in serializing data point " + data + ".");
        }
        return retVal;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, DataInstance dataInstance) {
        return serializeDataInstance(dataInstance);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, DataInstance data) {
        return serializeDataInstance(data);
    }

    @Override
    public void close() {

    }
}
