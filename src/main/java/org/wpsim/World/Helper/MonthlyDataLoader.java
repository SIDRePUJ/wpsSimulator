package org.wpsim.World.Helper;

import BESA.Util.FileLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wpsim.World.layer.data.MonthData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jairo
 */
public class MonthlyDataLoader {

    /**
     *
     * @param dataFileLocation The location of the file to load
     * @return  A list of MonthData objects
     * @throws IOException If the file is not found
     */
    public static List<MonthData> loadMonthlyDataFile(String dataFileLocation) throws IOException {
        String jsonContent = FileLoader.readFile(dataFileLocation);
        return jsonToMonthlyData(jsonContent, dataFileLocation);
    }

    private static List<MonthData> jsonToMonthlyData(String jsonContent, String dataFileLocation) {
        JSONArray radiationJson = new JSONArray(jsonContent);
        ArrayList<MonthData> monthlyData = new ArrayList<>();
        radiationJson.forEach(item -> {
            JSONObject currentObject = (JSONObject) item;
            MonthData monthData = new MonthData();
            monthData.setAverage(currentObject.getDouble("average"));
            monthData.setMaxValue(currentObject.getDouble("maxValue"));
            monthData.setMinValue(currentObject.getDouble("minValue"));
            monthData.setStandardDeviation(currentObject.getDouble("standardDeviation"));
            monthlyData.add(monthData);
        });
        return monthlyData;
    }
}
