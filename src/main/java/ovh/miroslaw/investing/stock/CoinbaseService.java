package ovh.miroslaw.investing.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import ovh.miroslaw.investing.model.Coinbase;
import ovh.miroslaw.investing.model.Portfolio;

import java.util.List;
import java.util.Optional;

import static ovh.miroslaw.investing.model.AssetType.CRYPTO;

public class CoinbaseService {

    public static final String BASE_URL = "https://api.coinbase.com";
    public static final String API_VERSION = "v2";
    private String exchangeCurrency = "USD";

    public CoinbaseService() {
    }

    public CoinbaseService(String exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }

    public List<Coinbase> getAssetsInfo(List<Portfolio> assets) {
        Unirest.config().defaultBaseUrl(BASE_URL).cookieSpec("standard");
        return assets.stream()
                .filter(e -> CRYPTO.equals(e.type()))
                .map(e -> getAssetInfo(e, exchangeCurrency))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<Coinbase> getAssetInfo(Portfolio portfolioAsset, String exchangeCurrency) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("/{version}/prices/{asset}-{exchangeCurrency}/spot")
                    .routeParam("version", API_VERSION)
                    .routeParam("exchangeCurrency", exchangeCurrency)
                    .routeParam("asset", portfolioAsset.assetSymbol())
                    .asJson();
            final Coinbase coinbase = new ObjectMapper()
                    .readValue(response.getBody().getObject().get("data").toString(), Coinbase.class);
            return Optional.of(new Coinbase(portfolioAsset.assetName(), coinbase.assetSymbol(),
                    coinbase.exchangeCurrency(), coinbase.price()));
        } catch (Exception e) {
            MarketFactory.printError(portfolioAsset.assetName());
            return Optional.empty();
        }
    }

}
