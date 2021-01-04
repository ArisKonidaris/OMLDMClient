package omlClient.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import statistics.platform.Flink.FlinkJob;
import statistics.platform.Flink.Vertex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JobStats {

    String flink_server;
    private Runtime rt;
    private String jobID;
    private ObjectMapper mapper;
    private Vertex workers;
    private String metricsFilters;


    public JobStats(boolean local, Runtime rt, String jobID, int parallelism) throws IOException {
        this.flink_server = local ? "localhost:8081" : ClusterConstants.FLINK_SERVER;
        this.rt = rt;
        this.jobID = jobID;
        this.mapper = new ObjectMapper();
        this.workers = getWorkers(getFlinkJob());
        this.metricsFilters = "FlinkSpoke.numRecordsInPerSecond" +
                ",FlinkSpoke.numRecordsOutPerSecond" +
                ",FlinkSpoke.numRecordsIn" +
                ",FlinkSpoke.numRecordsOut" +
                "&0-" + (parallelism - 1);
    }

    public String getStats() throws IOException {
        Process cmd = rt.exec("curl " + flink_server + "/jobs/" + jobID + "/vertices/" + workers.id +
                "/subtasks/metrics?get=" + metricsFilters);
        BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
        StringBuilder stats = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            stats.append(inputLine);
            if (reader.readLine() != null) stats.append("\n");
        }
        return stats.toString();
    }

    /**
     *  Get the Flink Job info.
     *
     * @return A FlinkJob object.
     * @throws IOException
     */
    public FlinkJob getFlinkJob() throws IOException {
        Process cmd = rt.exec("curl " + flink_server + "/jobs/" + jobID);
        BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
            if (reader.readLine() != null) response.append("\n");
        }

        return mapper.readValue(response.toString(), FlinkJob.class);
    }

    /**
     * Get the workers of the OMLDM job.
     *
     * @param flinkJob The OMLDM job.
     * @return The workers.
     */
    public Vertex getWorkers(FlinkJob flinkJob) {
        Vertex workers = null;
        for (Vertex vertex : flinkJob.vertices) if (vertex.name.startsWith("FlinkSpoke")) workers = vertex;
        assert workers != null;
        return workers;
    }

}
