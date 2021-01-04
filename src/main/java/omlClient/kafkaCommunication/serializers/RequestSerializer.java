package omlClient.kafkaCommunication.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import ControlAPI.Request;

import java.util.Map;

/**
 * A Kafka Serializer class for serializing the Request to
 * the Online Machine Leaning component.
 */
public class RequestSerializer implements Serializer<Request> {

    public byte[] serializeRequest(Request request) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(request);
        } catch (Exception e) {
            System.out.println("Error in serializing Request " + request + ".");
        }
        return retVal;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, Request request) {
        return serializeRequest(request);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Request data) {
        return serializeRequest(data);
    }

    @Override
    public void close() {

    }
}
