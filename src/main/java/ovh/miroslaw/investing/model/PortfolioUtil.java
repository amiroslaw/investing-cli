package ovh.miroslaw.investing.model;

import picocli.CommandLine.Help.Ansi;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PortfolioUtil {

    private PortfolioUtil() {
    }

    public static Map<String, BigDecimal> convertAssetsToNamePriceMap(List<? extends Asset> assets) {
        return assets.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.price() != null)
                .collect(Collectors.toMap(Asset::assetName, Asset::price));
    }

    public static Map<String, Portfolio> convertPortfolioToSymbolMap(List<Portfolio> portfolio) {
        return portfolio.stream()
                .collect(Collectors.toMap(Portfolio::assetName, p -> p));
    }

}
