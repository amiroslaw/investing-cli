package ovh.miroslaw.investing.stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.model.Yahoo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ovh.miroslaw.investing.model.AssetType.CC;
import static ovh.miroslaw.investing.model.AssetType.CRYPTO;
import static ovh.miroslaw.investing.model.AssetType.GPW;

public class YahooService {

    public static final String BASE_URL = "https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols=";
    private final String accessKey;
    private String exchangeCurrency = "USD";

    public YahooService(String accessKey) {
        this.accessKey = accessKey;
    }
    public YahooService(String accessKey, String exchangeCurrency) {
        this.accessKey = accessKey;
        this.exchangeCurrency = exchangeCurrency;
    }

    public List<Yahoo> getAssetsInfo(List<Portfolio> assets) {
        final List<String> assetSymbol = getAssetSymbol(assets);
        return fetchData(assetSymbol);
    }

    private List<Yahoo> fetchData(List<String> assetsSymbol) {
        try {
            final String assetsSymbolQuery = String.join(",", assetsSymbol);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + assetsSymbolQuery))
                    .header("x-api-key", accessKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, BodyHandlers.ofString());

            return deserializeJson(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            MarketFactory.printError("Yahoo api error");
            return Collections.emptyList();
        }
    }

    private List<Yahoo> deserializeJson(String response) throws JsonProcessingException {
        final JsonNode data = new ObjectMapper()
                .readTree(response).get("quoteResponse").get("result");

        List<Yahoo> assets = new ArrayList<>();
        for (com.fasterxml.jackson.databind.JsonNode assetNode : data) {
            final String shortName = assetNode.get("shortName").asText();
            final BigDecimal price = BigDecimal.valueOf(assetNode.get("regularMarketPrice").asDouble());
            final BigDecimal priceChangePercent = BigDecimal.valueOf(
                    assetNode.get("regularMarketChangePercent").asDouble()).setScale(2, RoundingMode.HALF_UP);
            final BigDecimal minPrice = BigDecimal.valueOf(assetNode.get("regularMarketDayLow").asDouble());
            final BigDecimal maxPrice = BigDecimal.valueOf(assetNode.get("regularMarketDayHigh").asDouble());
            final String fiftyTwoWeekRange = assetNode.get("fiftyTwoWeekRange").asText();
            assets.add(new Yahoo(shortName, price, minPrice, maxPrice, priceChangePercent, fiftyTwoWeekRange));
        }
        return assets;
    }

    private List<String> getAssetSymbol(List<Portfolio> assets) {
        final Stream<String> gpw = assets.stream()
                .filter(e -> GPW.equals(e.type()))
                .map(e -> e.assetSymbol() + ".WA");
        final Stream<String> cc = assets.stream()
                .filter(e -> CC.equals(e.type()))
                .map(Portfolio::assetSymbol);
        final Stream<String> crypto = assets.stream()
                .filter(e -> CRYPTO.equals(e.type()))
                .map(e -> e.assetSymbol() + "-" + exchangeCurrency);
        return Stream.concat(Stream.concat(gpw, crypto), cc)
                .collect(toList());
    }
}
