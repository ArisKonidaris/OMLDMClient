package omlClient.clients;

import omlClient.kafkaCommunication.KafkaConstants;
import omlClient.kafkaCommunication.consumers.OMLDMConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Scanner;

/**
 * A simple example of receiving responses from the
 * Online Machine Learning component.
 */
public class ResponseClient {

    public static void main(String[] args) {

        String topic; // The topic name.
        String brokerList; // The Kafka broker list.
        OMLDMConsumer kafkaConsumer = new OMLDMConsumer(); // The Kafka response consumer.

        // Check for arguments else set the default ones
        if (args.length == 2)
            try {
                topic = args[0];
                brokerList = args[1];
            } catch (Exception e) {
                throw new RuntimeException("Invalid arguments provided.");
            }
        else {
            topic = KafkaConstants.RESPONSE_TOPIC_NAME;
            brokerList = KafkaConstants.RESPONSE_KAFKA_BROKERS;
        }

        // Start the testing.
        try {

            // Create the Kafka response consumer.
            kafkaConsumer = OMLDMConsumer.setConsumer(topic, brokerList, KafkaConstants.RESPONSE_GROUP_ID);

            // This consumer polls records from the responses topic. If the topic stays silent for one.
            // minute, then it asks the user's permission to either terminate or resume consuming response records.
            Scanner myObj = new Scanner(System.in);
            while (true) {

                // Consume response records.
                long time = System.currentTimeMillis();
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.consumeRecords();
                    if (records.count() != 0) {
                        time = System.currentTimeMillis();
                        for (ConsumerRecord<String, String> record : records) {
                            System.out.println(record.value());
                        }
                    } else {
                        if ((System.currentTimeMillis() - time) > 60000) {
                            break;
                        }
                    }
                }

                // Asks the user whether to resume consuming responses records or not.
                String input = "";
                while (!input.equals("0") && !input.equals("1")) {
                    System.out.println("Press 0 to terminate the consumer or 1 to continue consuming responses.");
                    input = myObj.nextLine();
                }
                if (input.equals("0")) break;

            }

        } catch (Exception e) {
            System.out.println("Something went wrong.");
        } finally {
            // Ensure that you always close the Kafka consumer when you are done using it.
            kafkaConsumer.close();
        }

    }

}
