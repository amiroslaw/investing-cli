package ovh.miroslaw.investing.stock;

import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;

import java.util.List;
import java.util.function.BiFunction;

public enum MarketEnum {
    COINBASE((m, p) -> m.getExchangeCurrency()
            .map(CoinbaseService::new)
            .map(c -> c.getAssetsInfo(p))
            .orElse(new CoinbaseService().getAssetsInfo(p))
    ),
    YAHOO((m, p) ->
    {
        YahooService yahooService = m.getYahooService.apply(m.getExchangeCurrency(), m.getAccessKey());
        return yahooService.getAssetsInfo(p);
    }),
    BIZ((m, p) -> new BiznesRadarService().getAssetsInfo(p)),
    MARKETSTACK((m, p) -> m.getMarketstackAssets(p));

    final BiFunction<MarketFactory, List<Portfolio>, List<? extends Asset>> assetsInfo;

    MarketEnum(BiFunction<MarketFactory, List<Portfolio>, List<? extends Asset>> assetsInfo) {
        this.assetsInfo = assetsInfo;
    }
}
