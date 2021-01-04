package omlClient.cluster;

public class ClusterConstants {

    public static final String KAFKA_BROKER_1 = "clu02.softnet.tuc.gr";
    public static final String KAFKA_BROKER_2 = "clu03.softnet.tuc.gr";
    public static final String KAFKA_BROKER_3 = "clu04.softnet.tuc.gr";
    public static final String KAFKA_BROKER_4 = "clu06.softnet.tuc.gr";
    public static final String KAFKA_BROKER_PORT = "6667";

    public static final String ZOOKEEPER_SERVER_1 = "clu01.softnet.tuc.gr";
    public static final String ZOOKEEPER_SERVER_2 = "clu02.softnet.tuc.gr";
    public static final String ZOOKEEPER_SERVER_3 = "clu03.softnet.tuc.gr";
    public static final String ZOOKEEPER_SERVER_PORT  = "2182";

    public static final String KAFKA_PATH = "/usr/hdp/current/kafka-broker/bin/";

    public static final String CSV_DUMMY_MIL_E1_PATH = "/home/vkonidaris/Data/DummyDataSet_43f2c_mil_e1.txt";
    public static final String CSV_DUMMY_MIL_E2_PATH = "/home/vkonidaris/Data/DummyDataSet_43f2c_mil_e2.txt";
    public static final String CSV_DUMMY_MIL_E3_PATH = "/home/vkonidaris/Data/DummyDataSet_43f2c_mil_e3.txt";
    public static final String CSV_DUMMY_MIL_E5_PATH = "/home/vkonidaris/Data/DummyDataSet_43f2c_mil_e5.txt";
    public static final String CSV_DUMMY_MIL_E10_PATH = "/home/vkonidaris/Data/DummyDataSet_43f2c_mil_e10.txt";
    public static final String AMNIST_PATH = "/home/vkonidaris/Data/AMNIST.txt";

    public static final String KAFKA_BROKERS =
            KAFKA_BROKER_1 + ":" + KAFKA_BROKER_PORT + "," +
            KAFKA_BROKER_2 + ":" + KAFKA_BROKER_PORT + "," +
            KAFKA_BROKER_3 + ":" + KAFKA_BROKER_PORT + "," +
            KAFKA_BROKER_4 + ":" + KAFKA_BROKER_PORT;

    public static final String[] DATASETS = {
            CSV_DUMMY_MIL_E1_PATH,
            CSV_DUMMY_MIL_E2_PATH,
            CSV_DUMMY_MIL_E3_PATH,
            CSV_DUMMY_MIL_E5_PATH,
            CSV_DUMMY_MIL_E10_PATH,
            AMNIST_PATH
    };

    public static final String FLINK_PATH = "/usr/local/flink-1.10.0/";
    public static final String FLINK_RUN = FLINK_PATH + "bin/flink run";
    public static final String FLINK_LIST_JOBS = FLINK_PATH + "bin/flink list";
    public static final String FLINK_SERVER = "clu01.softnet.tuc.gr:8081";

}
