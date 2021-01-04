package omlClient.kafkaCommunication.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.Prediction;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * A Kafka Deserializer class for deserializing the
 * predictions of the Online Machine Leaning component.
 */
public class PredictionDeserializer implements Deserializer<Prediction> {

    public Prediction deserializePrediction(byte[] bytes) {
        Prediction dataPoint = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dataPoint = objectMapper.readValue(bytes, Prediction.class);
        } catch (Exception e) {
            System.out.println("Error in deserializing prediction.");
        }
        return dataPoint;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Prediction deserialize(String s, byte[] bytes) {
        return deserializePrediction(bytes);
    }

    @Override
    public Prediction deserialize(String topic, Headers headers, byte[] data) {
        return deserializePrediction(data);
    }

    @Override
    public void close() {

    }
}
