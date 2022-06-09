package ovh.miroslaw.investing.portfolio;

import ovh.miroslaw.investing.model.AlertCondition;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.model.Portfolio.Alert;
import ovh.miroslaw.investing.model.PortfolioUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ovh.miroslaw.investing.model.AlertCondition.ABOVE;
import static ovh.miroslaw.investing.model.AlertCondition.BELOW;
import static ovh.miroslaw.investing.model.PortfolioUtil.getAssetPrice;

public class AlertChecker {

    private final String exchangeCurrency;

    public AlertChecker(String exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }

    public String checkAlerts(List<Portfolio> portfolio, List<? extends Asset> assets) {
        final Map<String, BigDecimal> assetsMap = PortfolioUtil.convertAssetsToNamePriceMap(assets);
        return portfolio.stream()
                .filter(e -> !e.alerts().isEmpty())
                .map(e -> checkAlert(e, getAssetPrice(exchangeCurrency, assetsMap, e)))
                .flatMap(Optional::stream)
                .collect(Collectors.joining());
    }

    private Optional<String> checkAlert(Portfolio portfolio, BigDecimal currentPrice) {
        if (currentPrice == null) {
            return Optional.empty();
        }

        for (Alert alert : portfolio.alerts()) {
            final boolean priceIsAboveAlert =
                    alert.alertCondition() == ABOVE && currentPrice.compareTo(alert.price()) >= 0;
            final boolean priceIsBelowAlert = alert.alertCondition() == AlertCondition.BELOW
                    && currentPrice.compareTo(alert.price()) <= 0;
            if (priceIsAboveAlert) {
                return Optional.of(createMsg(portfolio.assetName(), currentPrice, alert.price(), ABOVE));
            } else if (priceIsBelowAlert) {
                return Optional.of(createMsg(portfolio.assetName(), currentPrice, alert.price(), BELOW));
            }
        }
        return Optional.empty();
    }

    private String createMsg(String assetName, BigDecimal currentPrice, BigDecimal alertPrice,
            AlertCondition condition) {
        return """
                %s (%.0f) is %s %.0f;
                """.formatted(assetName, currentPrice, condition, alertPrice);
    }
}
