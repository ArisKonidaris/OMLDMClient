package omlClient.kafkaCommunication;

/**
 * Kafka static final parameters used to set up Kafka client properties
 * for communicating with the Online Machine Learning component
 */
public class KafkaConstants {

    public static final String KAFKA_BROKER_1 = "localhost:9092";
    public static final String KAFKA_BROKER_2 = "localhost:9093";
    public static final String KAFKA_BROKER_3 = "localhost:9094";
    public static final String KAFKA_BROKER_4 = "localhost:9095";
    public static final String KAFKA_BROKERS_LIST = KAFKA_BROKER_1 +
            "," + KAFKA_BROKER_2 +
            "," + KAFKA_BROKER_3 +
            "," + KAFKA_BROKER_4;

    public static final String DATA_TOPIC_NAME = "trainingData";
    public static final String DATA_KAFKA_BROKERS = "localhost:9092";
    public static final String DATA_CLIENT_ID = "data_client_1";

    public static final String FORECASTING_TOPIC_NAME = "forecastingData";
    public static final String FORECASTING_KAFKA_BROKERS = "localhost:9092";
    public static final String FORECASTING_CLIENT_ID = "forecasting_client_1";

    public static final String REQUEST_TOPIC_NAME = "requests";
    public static final String REQUEST_KAFKA_BROKERS = "localhost:9092";
    public static final String REQUEST_CLIENT_ID = "request_client_1";

    public static final String RESPONSE_TOPIC_NAME = "responses";
    public static final String RESPONSE_KAFKA_BROKERS = "localhost:9092";
    public static final String RESPONSE_GROUP_ID = "response_group_1";

    public static final String PREDICTION_TOPIC_NAME = "predictions";
    public static final String PREDICTION_KAFKA_BROKERS = "localhost:9092";
    public static final String PREDICTION_GROUP_ID = "predictions_group_1";

    public static final String PERFORMANCE_TOPIC_NAME = "performance";
    public static final String PERFORMANCE_KAFKA_BROKERS = "localhost:9092";
    public static final String PERFORMANCE_GROUP_ID = "performance_group_1";

    /////////////////////// Other useful parameters (currently not used) for a Kafka client ////////////////////////////
    public static final String ACKS = "all";
    public static final Integer RETRIES = 0;
    public static final Integer BATCH_SIZE = 16384;
    public static final Integer LINGER_MS = 0;
    public static final Integer BUFFER_MEMORY = 33554432;
    public static final String OFFSET_RESET_LATEST = "latest";
    public static final String OFFSET_RESET_EARLIER = "earliest";
    public static final Integer MAX_POLL_RECORDS = 1;
}
