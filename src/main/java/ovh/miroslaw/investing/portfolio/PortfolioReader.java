package ovh.miroslaw.investing.portfolio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.RetrievingDataException.ErrorCode;
import ovh.miroslaw.investing.model.AlertCondition;
import ovh.miroslaw.investing.model.AssetType;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.model.Portfolio.Alert;
import ovh.miroslaw.investing.model.Portfolio.Holding;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PortfolioReader {

    private PortfolioReader() {
    }

    public static final String PORTFOLIO_FILE_NAME = "portfolio.json";
    public static final String CONFIG_FILE_NAME = "investing";

    public static List<Portfolio> getPortfolio(File portfolioConfig) throws RetrievingDataException {
        try {
            return deserialize(portfolioConfig);
        } catch (Exception e) {
            final String errorMsg = "Error with reading portfolio file from: " + portfolioConfig.getAbsolutePath();
            throw new RetrievingDataException(errorMsg, ErrorCode.SOFTWARE);
        }
    }

    public static Path getConfigPath() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        final Map<String, String> environment = processBuilder.environment();
        final Optional<String> xdgConfigHome = Optional.of(environment.get("XDG_CONFIG_HOME"));
        final String home = environment.get("HOME");
        return Path.of(xdgConfigHome.map(e -> e + "/" + CONFIG_FILE_NAME)
                .orElse(home + ".") + "/" + PORTFOLIO_FILE_NAME);
    }

    private static List<Portfolio> deserialize(File portfolioConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode assets = mapper.readTree(portfolioConfig);
        List<Portfolio> portfolio = new ArrayList<>();
        for (JsonNode asset : assets) {
            final String assetName = asset.get("assetName").asText();
            final String assetSymbol = asset.get("assetSymbol").asText();

            final AssetType assetType = AssetType.valueOf(asset.get("type").asText("GPW"));
            final JsonNode alertsNode = asset.get("alerts");
            final JsonNode holdingsNode = asset.get("holdings");
            final ArrayList<Alert> alerts = getAlerts(alertsNode);
            final ArrayList<Holding> holdings = getHoldings(holdingsNode);
            portfolio.add(new Portfolio(assetName, assetSymbol, assetType, holdings, alerts));
        }
        return portfolio;
    }

    private static ArrayList<Holding> getHoldings(JsonNode holdingsNode) {
        final ArrayList<Portfolio.Holding> holdings = new ArrayList<>();
        if (holdingsNode == null) {
            return new ArrayList<>();
        }
        for (JsonNode holding : holdingsNode) {
            BigDecimal buyPrice = null;
            if (holding.get("buyPrice") != null) {
                buyPrice = BigDecimal.valueOf(holding.get("buyPrice").asDouble());
            }
            BigDecimal sellPrice = null;
            if (holding.get("sellPrice") != null) {
                sellPrice = BigDecimal.valueOf(holding.get("sellPrice").asDouble());
            }
            final double amount = holding.get("amount").asDouble();
            holdings.add(new Holding(buyPrice, sellPrice, amount));
        }
        return holdings;
    }

    private static ArrayList<Portfolio.Alert> getAlerts(JsonNode alertsNode) {
        if (alertsNode == null) {
            return new ArrayList<>();
        }
        final ArrayList<Portfolio.Alert> alerts = new ArrayList<>();
        for (JsonNode alert : alertsNode) {
            final AlertCondition alertCondition = AlertCondition.valueOf(alert.get("alertCondition").asText("ABOVE"));
            final BigDecimal price = BigDecimal.valueOf(alert.get("price").asDouble());
            alerts.add(new Alert(price, alertCondition));
        }
        return alerts;
    }
}
