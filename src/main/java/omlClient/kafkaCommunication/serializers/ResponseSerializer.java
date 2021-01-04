package omlClient.kafkaCommunication.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.QueryResponse;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * A Kafka Serializer class for serializing the Response of
 * the Online Machine Leaning component. This class is not
 * actually used in the client. It is used inside the Online
 * Machine Learning component in Flink.
 */
public class ResponseSerializer implements Serializer<QueryResponse> {

    public byte[] serializeResponse(QueryResponse response) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(response);
        } catch (Exception e) {
            System.out.println("Error in serializing Response " + response + ".");
        }
        return retVal;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, QueryResponse response) {
        return serializeResponse(response);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, QueryResponse data) {
        return serializeResponse(data);
    }

    @Override
    public void close() {

    }
}
