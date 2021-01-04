package omlClient.kafkaCommunication.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.QueryResponse;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * A Kafka Deserializer class for deserializing the
 * responses of the Online Machine Leaning component.
 */
public class ResponseDeserializer implements Deserializer<QueryResponse> {

    public QueryResponse deserializeResponse(byte[] bytes) {
        QueryResponse response = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(bytes, QueryResponse.class);
        } catch (Exception e) {
            System.out.println("Error in deserializing Response.");
        }
        return response;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public QueryResponse deserialize(String s, byte[] bytes) {
        return deserializeResponse(bytes);
    }

    @Override
    public QueryResponse deserialize(String topic, Headers headers, byte[] data) {
        return deserializeResponse(data);
    }

    @Override
    public void close() {

    }
}
