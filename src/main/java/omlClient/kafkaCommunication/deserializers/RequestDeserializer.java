package omlClient.kafkaCommunication.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import ControlAPI.Request;

import java.util.Map;

/**
 * A Kafka Deserializer class for deserializing the Requests
 * in the Online Machine Leaning component. This class is not
 * actually used in the client. It is used inside the Online
 * Machine Learning component in Flink.
 */
public class RequestDeserializer implements Deserializer<Request> {

    public Request deserializeRequest(byte[] bytes) {
        Request request = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            request = objectMapper.readValue(bytes, Request.class);
        } catch (Exception e) {
            System.out.println("Error in deserializing Request.");
        }
        return request;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Request deserialize(String s, byte[] bytes) {
        return deserializeRequest(bytes);
    }

    @Override
    public Request deserialize(String topic, Headers headers, byte[] data) {
        return deserializeRequest(data);
    }

    @Override
    public void close() {

    }
}
