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

public class AlertChecker {

    private AlertChecker() {
    }

    public static String checkAlerts(List<Portfolio> portfolio, List<? extends Asset> assets) {
        final Map<String, BigDecimal> assetsMap = PortfolioUtil.convertAssetsToNamePriceMap(assets);
        return portfolio.stream()
                .filter(e -> !e.alerts().isEmpty())
                .map(e -> checkAlert(e, assetsMap.get(e.assetName())))
                .flatMap(Optional::stream)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static Optional<String> checkAlert(Portfolio portfolio, BigDecimal currentPrice) {
        for (Alert alert : portfolio.alerts()) {
            final boolean priceIsAboveAlert =
                    alert.alertCondition() == ABOVE && currentPrice.compareTo(alert.price()) >= 0;
            final boolean priceIsBelowAlert = alert.alertCondition() == AlertCondition.BELOW
                    && currentPrice.compareTo(alert.price()) <= 0;
            if (priceIsAboveAlert) {
                return Optional.of(createMsg(portfolio.assetName(), alert.price(), ABOVE));
            } else if (priceIsBelowAlert) {
                return Optional.of(createMsg(portfolio.assetName(), alert.price(), BELOW));
            }
        }
        return Optional.empty();
    }

    private static String createMsg(String assetName, BigDecimal alert, AlertCondition condition) {
        return assetName + " is " + condition + " " + alert;
    }
}
