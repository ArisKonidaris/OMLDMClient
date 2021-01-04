package omlClient.clients;

import ControlAPI.Request;
import omlClient.kafkaCommunication.KafkaConstants;
import omlClient.kafkaCommunication.producers.RequestProducer;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.Scanner;

/**
 * A simple example of sending requests to the Online
 * Machine Learning component via a provided json file.
 */
public class RequestClient {

    static int parseInt(String strInt) {
        try {
            return Integer.parseInt(strInt);
        } catch (Exception e) {
            
            return -1;
        }
    }

    public static void main(String[] args) {

        String topic; // The topic name.
        String brokerList; // The Kafka broker list.
        String filepath; // The filepath of the json file, containing the requests.
        RequestProducer kafkaProducer = new RequestProducer(); // The Kafka request producer.

        // Check for arguments else set the default ones
        if (args.length == 3)
            try {
                topic = args[0];
                brokerList = args[1];
                filepath = args[2];
            } catch (Exception e) {
                throw new RuntimeException("Invalid arguments provided.");
            }
        else {
            topic = KafkaConstants.REQUEST_TOPIC_NAME;
            brokerList = KafkaConstants.REQUEST_KAFKA_BROKERS;
//            brokerList = KafkaConstants.KAFKA_BROKERS_LIST;
            if (SystemUtils.IS_OS_LINUX)
                filepath = new File("").getAbsolutePath() + "/data/RequestCNN.json";
            else if (SystemUtils.IS_OS_WINDOWS)
                filepath = new File("").getAbsolutePath() + "\\data\\RequestCNN.json";
            else
                throw new RuntimeException("Incompatible operating system. " +
                        "Run the project on a LINUX or a Windows OS.");
        }

        // Start the testing
        try {

            // Create the kafka omlClient.request producer
            kafkaProducer = kafkaProducer.setProducer(topic, brokerList, 1);

            // Send the requests to the requests topic
            kafkaProducer.sendRequestsFromFile(filepath, true);

            Scanner myObj = new Scanner(System.in);

            long requestID = 0;
            while (true) {
                int id = parseInt("-1");
                while (id < 0) {
                    System.out.println("Provide a positive ML Pipeline id to query " +
                            "or press 0 to terminate the producer.");
                    id = parseInt(myObj.nextLine());
                }
                if (id > 0) {
                    kafkaProducer.sendJsonStringAsRequest(new Request(id, requestID).toString(), true);
                    requestID++;
                } else break;
            }

        } catch (Exception e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();
        } finally {
            // Ensure that you always close the Kafka producer when you are done using it.
            kafkaProducer.close();
        }

    }

}
