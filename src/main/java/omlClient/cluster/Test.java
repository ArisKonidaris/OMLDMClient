package omlClient.cluster;

import omlClient.clients.RequestClient;
import omlClient.clients.TrainingDataClient;
import statistics.platform.Flink.FlinkJob;

import java.io.*;

public class Test {

    /**
     * A method for checking if a Kafka topic already exists.
     *
     * @param rt    An instance of class [[Runtime]].
     * @param topic The topic name.
     * @return True if the given topic exists.
     */
    public static boolean topicExists(Runtime rt, String topic, String zookeeperBroker) throws IOException {

        String kafka_path = ClusterConstants.KAFKA_PATH;
        String zoo_port = ClusterConstants.ZOOKEEPER_SERVER_PORT;
        String topic_list_command = kafka_path + "kafka-topics.sh --list --zookeeper " + zookeeperBroker + ":" + zoo_port;

        // Run Kafka command to list all the available topics.
        Process topicList = rt.exec(topic_list_command);

        // Parse the answer to check if the topic exists.
        BufferedReader in = new BufferedReader(new InputStreamReader(topicList.getInputStream()));
        String inputLine;
        boolean exists = false;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.equals(topic)) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public static void createAndWriteDataToTopic(Runtime rt,
                                                 String topic,
                                                 int partitions,
                                                 String zookeeperBroker,
                                                 int rep_factor,
                                                 String brokerList,
                                                 String dataPath) throws IOException, InterruptedException {
        if (!topicExists(rt, topic, zookeeperBroker)) {
            createTopic(rt, topic, partitions, zookeeperBroker, rep_factor);
            TrainingDataClient.main(new String[]{topic, brokerList, partitions + "", dataPath});
        }
    }

    public static void createAndWriteRequestsToTopic(Runtime rt,
                                                     String topic,
                                                     int partitions,
                                                     String zookeeperBroker,
                                                     int rep_factor,
                                                     String brokerList,
                                                     String filePath) throws IOException, InterruptedException {
        if (!topicExists(rt, topic, zookeeperBroker)) {
            createTopic(rt, topic, partitions, zookeeperBroker, rep_factor);
            RequestClient.main(new String[]{topic, brokerList, filePath});
        }
    }

    /**
     * A method for creating a Kafka topic.
     *
     * @param rt         An instance of class [[Runtime]].
     * @param topic      The name of the topic to be created.
     * @param partitions The number of partitions of the topic to be created.
     * @param rep_factor The replication factor of the topic to be created.
     */
    public static void createTopic(Runtime rt,
                                   String topic,
                                   int partitions,
                                   String zookeeperBroker,
                                   int rep_factor)
            throws IOException, InterruptedException {
        System.out.println("Creating topic " + topic);
        while (!topicExists(rt, topic, zookeeperBroker)) {
            String kafka_path = ClusterConstants.KAFKA_PATH;
            String zoo_port = ClusterConstants.ZOOKEEPER_SERVER_PORT;
            String create_topic_command = kafka_path + "kafka-topics.sh --create --zookeeper " + zookeeperBroker +
                    ":" + zoo_port + " --replication-factor " + rep_factor +
                    " --partitions " + partitions + " --topic " + topic;
            rt.exec(create_topic_command);
            Thread.sleep(5000);
        }
        System.out.println("Topic " + topic + " created.");
    }

    /**
     * Starts a new flink job and returns its job id.
     *
     * @param rt                   An instance of class [[Runtime]].
     * @param startFlinkJobCommand The command to run in order to create the new job.
     * @return The Job ID of the newly created job.
     */
    public static String startJob(Runtime rt, String startFlinkJobCommand)
            throws IOException, InterruptedException {
        Process start_job = rt.exec(startFlinkJobCommand);
        Thread.sleep(5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(start_job.getInputStream()));
        String inputLine;
        String jobId = "";
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.startsWith("Job has been submitted with JobID ")) {
                jobId = inputLine.substring(34);
                break;
            }
        }
        return jobId;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        assert (args.length == 4);

        // The path of the JAR file.
        String JAR_PATH = args[0];

        // The name of the dataset.
        String dataSetName = args[1];

        // The number of Machine Learning Pipelines that are going to be trained on the OMLDM jobs.
        String numOfMLPs = args[2];

        // The request file name.
        String request = args[3];

        // Get the filepath of this java test.
        String filepath = new File("").getAbsolutePath();

        // Create a Runtime instance to execute command lines.
        Runtime rt = Runtime.getRuntime();

        // All the necessary topics for the testing of the OMLDM component.
        String[] topics = {"trainingData", "requests", "responses", "psMessages", "performance"};
//        String[] topics = {"trainingData", "requests", "responses", "psMessages", "performance", "forecastingData", "predictions"};

        // The parallelism tests to be executed.
        Integer[] parallelism = {1, 2, 4, 8, 16, 32};

        // Setting up the Kafka topics.
        System.out.println("Setting up the trainingData topic.");
        createAndWriteDataToTopic(rt,
                topics[0],
                32,
                ClusterConstants.ZOOKEEPER_SERVER_1,
                4,
                ClusterConstants.KAFKA_BROKERS,
                ClusterConstants.CSV_DUMMY_MIL_E1_PATH);
        System.out.println("trainingData topic is all set up.");

        System.out.println("Setting up the requests topic.");
        createAndWriteRequestsToTopic(rt,
                topics[1],
                1,
                ClusterConstants.ZOOKEEPER_SERVER_1,
                4,
                ClusterConstants.KAFKA_BROKERS,
                filepath + "/data/" + request);
        System.out.println("requests topic is all set up.");

        System.out.println("Setting up the responses topic.");
        createTopic(rt, topics[2], 1, ClusterConstants.ZOOKEEPER_SERVER_2, 4);
        System.out.println("responses topic is all set up.");

        System.out.println("Setting up the psMessages topic.");
        createTopic(rt, topics[3], 32, ClusterConstants.ZOOKEEPER_SERVER_3, 4);
        System.out.println("psMessages topic is all set up.");

        System.out.println("Setting up the performance topic.");
        createTopic(rt, topics[4], 1, ClusterConstants.ZOOKEEPER_SERVER_2, 4);
        System.out.println("performance topic is all set up.");

        // Run the OMLDM tests.
        StringBuilder results = new StringBuilder();
        for (Integer workers : parallelism) {

            // Create the OMLDM job test run command.
            String jobName = "OMLDM_" + dataSetName + "_" + numOfMLPs + "_" + workers;
            String startFlinkJobCommand = ClusterConstants.FLINK_RUN + " " + JAR_PATH + " --parallelism " + workers +
                    " --trainingDataTopic " + topics[0] + " --trainingDataAddr " + ClusterConstants.KAFKA_BROKERS +
                    " --requestsTopic " + topics[1] + " --requestsAddr " + ClusterConstants.KAFKA_BROKERS +
                    " --responsesTopic " + topics[2] + " --responsesAddr " + ClusterConstants.KAFKA_BROKERS +
                    " --psMessagesTopic " + topics[3] + " --psMessagesAddr " + ClusterConstants.KAFKA_BROKERS +
                    " --performanceTopic " + topics[4] + " --performanceAddr " + ClusterConstants.KAFKA_BROKERS +
                    " --jobName " + jobName;
            System.out.println("Initializing Flink job " + jobName);

            // Start the OMLDM test.
            String jobID = startJob(rt, startFlinkJobCommand);

            // Check if the job has been terminated. If yes, then print the statistics of the job.
            JobStats stats = new JobStats(false, rt, jobID, workers);
            while (true) {
                Thread.sleep(5000);
                FlinkJob flinkJob = stats.getFlinkJob();
                if (flinkJob.state.equals("FAILED"))
                    break;
            }
        }

        // Start the test.
        System.out.println("Stop testing...");

    }

}
