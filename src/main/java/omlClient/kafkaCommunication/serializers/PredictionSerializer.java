package omlClient.kafkaCommunication.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.Prediction;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * A Kafka Serializer class for serializing the Prediction objects.
 * This class is not actually used in the client. It is used inside
 * the Online Machine Learning component in Flink.
 */
public class PredictionSerializer implements Serializer<Prediction> {

    public byte[] serializePrediction(Prediction prediction) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(prediction);
        } catch (Exception e) {
            System.out.println("Error in serializing Prediction " + prediction + ".");
        }
        return retVal;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, Prediction prediction) {
        return serializePrediction(prediction);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Prediction data) {
        return serializePrediction(data);
    }

    @Override
    public void close() {

    }
}