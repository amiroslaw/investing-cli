package ovh.miroslaw.investing.model;

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
                .collect(Collectors.toMap(asset -> splitSymbolFromMarket(asset.symbol()), Asset::price));
    }

    public static String splitSymbolFromMarket(String symbol) {
        final String[] split = symbol.split("\\.");
        return split[0];
    }

    public static BigDecimal getAssetPrice(String exchangeCurrency, Map<String, BigDecimal> assetsMap,
            Portfolio portfolio) {
        String assetSymbol = portfolio.assetSymbol();
        if (portfolio.type() == AssetType.CRYPTO) {
            assetSymbol += "-" + exchangeCurrency;
        }
        return assetsMap.get(assetSymbol);
    }

}
