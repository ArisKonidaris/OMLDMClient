package omlClient.clients;

import omlClient.kafkaCommunication.KafkaConstants;
import omlClient.kafkaCommunication.producers.ForecastingDataStreamProducer;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;

/**
 * A simple example of sending unlabeled data to the Online
 * Machine Learning component for prediction, via a provided csv file.
 */
public class ForecastingDataClient {

    public static void main(String[] args) {

        String topic; // The topic name.
        String brokerList; // The Kafka broker list.
        int partitions; // The partitions of the Kafka data topic.
        String filepath; // The filepath of the json file, containing the data in csv format.
        ForecastingDataStreamProducer kafkaProducer = new ForecastingDataStreamProducer(); // The Kafka data producer.

        // Check for arguments else set the default ones
        if (args.length == 4)
            try {
                topic = args[0];
                brokerList = args[1];
                partitions = Integer.parseInt(args[2]);
                filepath = args[3];
            } catch (Exception e) {
                throw new RuntimeException("Invalid arguments provided.");
            }
        else {
            topic = KafkaConstants.FORECASTING_TOPIC_NAME;
            brokerList = KafkaConstants.FORECASTING_KAFKA_BROKERS;
            partitions = 16;
            if (SystemUtils.IS_OS_LINUX)
                filepath = new File("").getAbsolutePath() + "/data/forecastingData.csv";
            else if (SystemUtils.IS_OS_WINDOWS)
                filepath = new File("").getAbsolutePath() + "\\data\\forecastingData.csv";
            else
                throw new RuntimeException("Incompatible operating system. " +
                        "Run the project on a LINUX or a Windows OS.");
        }

        // Start the testing
        try {

            // Create the kafka data producer
            kafkaProducer = kafkaProducer.setProducer(topic, brokerList, partitions);

            // Send the data to the fata topic
            kafkaProducer.sendDataPointsFromCSVFile(filepath, true);

        } catch (Exception e) {
            System.out.println("Something went wrong.");
        } finally {
            // Ensure that you always close the Kafka producer when you are done using it.
            kafkaProducer.close();
        }

    }

}
