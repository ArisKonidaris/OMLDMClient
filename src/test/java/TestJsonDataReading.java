import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ControlAPI.DataInstance;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestJsonDataReading {

    /**
     * This tests reading valid JSON data from file.
     */
    @Test
    void testReadingValidJsonDataFile() throws IOException {

        int valid = 0;

        String filepath = "";
        if (SystemUtils.IS_OS_LINUX)
            filepath = new File("").getAbsolutePath() + "/data/validData.json";
        else if (SystemUtils.IS_OS_WINDOWS)
            filepath = new File("").getAbsolutePath() + "\\data\\validData.json";
        else
            throw new RuntimeException("Incompatible operating system. " +
                    "Run the project on a LINUX or a Windows OS.");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filepath));

        if (!root.isArray()) {
            throw new RuntimeException("A JSON Array is expected");
        } else {
            for (DataInstance dataPoint : mapper.convertValue(root, DataInstance[].class))
                if (dataPoint.isValid()){
                    valid++;
                    System.out.println(dataPoint);
                }
        }
        assert(valid == 20);
    }

    /**
     * This tests reading invalid JSON data from file.
     */
    @Test
    void testReadingInvalidJsonDataFile() throws IOException {

        int invalid = 14;

        String filepath = "";
        if (SystemUtils.IS_OS_LINUX)
            filepath = new File("").getAbsolutePath() + "/data/invalidData.json";
        else if (SystemUtils.IS_OS_WINDOWS)
            filepath = new File("").getAbsolutePath() + "\\data\\invalidData.json";
        else
            throw new RuntimeException("Incompatible operating system. " +
                    "Run the project on a LINUX or a Windows OS.");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filepath));
        if (!root.isArray()) {
            throw new RuntimeException("A JSON Array is expected");
        } else {
            for (DataInstance dataPoint : mapper.convertValue(root, DataInstance[].class))
                if (!dataPoint.isValid())
                    invalid--;
                else
                    System.out.println(dataPoint);
        }
        assert(invalid == 0);
    }

}