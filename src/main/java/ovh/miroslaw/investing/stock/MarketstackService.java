package ovh.miroslaw.investing.stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.RetrievingDataException.ErrorCode;
import ovh.miroslaw.investing.model.Marketstack;
import ovh.miroslaw.investing.model.Portfolio;
import picocli.CommandLine.Help.Ansi;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarketstackService {

    public static final String BASE_URL = "http://api.marketstack.com";
    private String accessKey;

    public MarketstackService(String accessKey) {
        this.accessKey = accessKey;
    }

    public List<Marketstack> getAssetsInfo(List<Portfolio> portfolio) throws RetrievingDataException {
        Unirest.config().defaultBaseUrl(BASE_URL);
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("/{version}/{duration}/latest")
                    .routeParam("version", "v1")
                    .routeParam("duration", "eod")
                    .queryString("access_key", accessKey)
                    .queryString("symbols", getAssetsQuery(portfolio))
                    .asJson();
        } catch (UnirestException e) {
            final String errorMsg = Ansi.AUTO.string("@|bold,red " + e.getMessage() + "!|@");
            throw new RetrievingDataException(errorMsg, ErrorCode.SOFTWARE);
        }
        if (response != null && response.isSuccess()) {
            final JSONArray data = response.getBody()
                    .getObject()
                    .getJSONArray("data");
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper
                        .readValue(data.toString(), new TypeReference<List<Marketstack>>() {
                        });
            } catch (JsonProcessingException e) {
                final String errorMsg = Ansi.AUTO.string("@|bold,red " + e.getMessage() + "!|@");
                throw new RetrievingDataException(errorMsg, ErrorCode.SOFTWARE);
            }
        }
        return Collections.emptyList();
    }

    private String getAssetsQuery(List<Portfolio> assets) {
        return assets.stream()
                .map(Portfolio::assetSymbol)
                .collect(Collectors.joining(","));
    }
}
