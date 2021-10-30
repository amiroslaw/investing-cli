package ovh.miroslaw.investing.portfolio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PortfolioReader {

    private PortfolioReader() {
    }

    public static final String PORTFOLIO_FILE_NAME = "portfolio";
    public static final String CONFIG_FILE_NAME = "investing";

    public static List<Portfolio> getPortfolio(File portfolioConfig) throws RetrievingDataException {
        try {
            final String fileExtension = FilenameUtils.getExtension(portfolioConfig.getName());
            final ConfigExtension.configExt configExt = ConfigExtension.configExt.valueOf(fileExtension.toUpperCase());
            return deserialize(portfolioConfig, configExt);
        } catch (Exception e) {
            e.printStackTrace();
            final String errorMsg = "Error with reading portfolio file from: " + portfolioConfig.getAbsolutePath();
            throw new RetrievingDataException(errorMsg, ErrorCode.SOFTWARE);
        }
    }

    public static Path getConfigPath() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        final Map<String, String> environment = processBuilder.environment();
        final Optional<String> xdgConfigHome = Optional.of(environment.get("XDG_CONFIG_HOME"));
        final String home = environment.get("HOME");
        final String pathWithoutExtension =
                xdgConfigHome.map(e -> e + File.separator + CONFIG_FILE_NAME + File.separator)
                        .orElse(home + "./") + PORTFOLIO_FILE_NAME + ".";
        final Path yamlPath = Path.of(pathWithoutExtension + ConfigExtension.configExt.YAML.name().toLowerCase());
        if (Files.exists(yamlPath)) {
            return yamlPath;
        } else {
            return Path.of(pathWithoutExtension + ConfigExtension.configExt.JSON.name().toLowerCase());
        }
    }

    private static List<Portfolio> deserialize(File portfolioConfig, ConfigExtension.configExt ext) throws IOException {
        ObjectMapper mapper = new ObjectMapper(ext.getFactory());
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
