package omlClient.cluster;

import statistics.platform.Flink.FlinkJob;

import java.io.IOException;

public class PrintStats {

    public static void main(String[] args) throws IOException {
        String jobID = "f886940877f80ba7f5f6fb2e7c97e33f";
        Runtime rt = Runtime.getRuntime();
        JobStats stats = new JobStats(true, rt, jobID, 1);

        FlinkJob flinkJob = stats.getFlinkJob();
        double duration = (flinkJob.duration - 10000.0) / 1000.0;
        double throughput = 1003918.0 / duration;
        System.out.println("Duration: " + duration);
        System.out.println("Throughput: " + throughput);
        System.out.println("State: " + flinkJob.state);
    }

}
