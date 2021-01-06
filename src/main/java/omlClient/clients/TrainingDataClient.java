package omlClient.clients;

import omlClient.kafkaCommunication.KafkaConstants;
import omlClient.kafkaCommunication.producers.TrainingDataStreamProducer;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A simple example of sending training data to the Online
 * Machine Learning component via a provided csv file.
 */
public class TrainingDataClient {

    public static void main(String[] args) {

        String topic; // The topic name.
        String brokerList; // The Kafka broker list.
        int partitions; // The partitions of the Kafka data topic.
        String filepath; // The filepath of the json file, containing the data in csv format.
        TrainingDataStreamProducer kafkaProducer = new TrainingDataStreamProducer(); // The Kafka data producer.

        // Check for arguments else set the default ones.
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
            topic = KafkaConstants.DATA_TOPIC_NAME;
            brokerList = KafkaConstants.DATA_KAFKA_BROKERS;
//            brokerList = KafkaConstants.KAFKA_BROKERS_LIST;
            partitions = 16;
            if (SystemUtils.IS_OS_LINUX)
//                filepath = new File("").getAbsolutePath() + "/data/trainingData.csv";
//                filepath = "/home/aris/IdeaProjects/DataStream/DummyDataSet_43f2c_mil_e1.txt";
                  filepath = "/home/aris/PycharmProjects/AMNIST.txt";
            else if (SystemUtils.IS_OS_WINDOWS)
//                filepath = new File("").getAbsolutePath() + "\\data\\DummyDataSet_43f2c_mil_e1.txt";
                filepath = new File("").getAbsolutePath() + "\\data\\AMNIST.txt";
            else
                throw new RuntimeException("Incompatible operating system. " +
                        "Run the project on a LINUX or a Windows OS.");
        }

        // Start the testing
        try {

            // Create the kafka data producer
            kafkaProducer = kafkaProducer.setProducer(topic, brokerList, partitions);

            // Send the data to the data topic
            kafkaProducer.sendDataPointsFromCSVFile(filepath, true);
            kafkaProducer.sendEndOfStream();
            System.out.println("Wrote the data set to the trainingData topic.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong. Terminating the training data client.");
        } finally {
            // Ensure that you always close the Kafka producer when you are done using it.
            kafkaProducer.close();
        }

    }

}
