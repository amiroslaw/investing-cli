package ovh.miroslaw.investing.stock;

import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static ovh.miroslaw.investing.Constant.ARG_ERROR;
import static ovh.miroslaw.investing.Constant.ARG_EXCHANGE_CURRENCY;
import static ovh.miroslaw.investing.Constant.ARG_KEY;
import static ovh.miroslaw.investing.Constant.getCommandParameter;

public class MarketFactory {

    private final Optional<String> exchangeCurrency;
    Optional<String> accessKey;
    private static boolean errorOption;
    final BiFunction<Optional<String>, String, YahooService> getYahooService = (currency, key) -> currency
            .map(e -> new YahooService(key, e))
            .orElse(new YahooService(key));

    public MarketFactory(CommandSpec commandSpec) {
        exchangeCurrency = getCommandParameter(commandSpec, ARG_EXCHANGE_CURRENCY.value).flatMap(ArgSpec::getValue);
        accessKey = getCommandParameter(commandSpec, ARG_KEY.value).flatMap(ArgSpec::getValue);
        errorOption = getCommandParameter(commandSpec, ARG_ERROR.value)
                .map(e -> (Boolean) e.getValue())
                .orElse(true);
    }

    public List<Asset> getAssets(List<Portfolio> portfolio, MarketEnum market) {
        return (List<Asset>) market.assetsInfo.apply(this, portfolio);
    }

    public List<Asset> getAssets(List<Portfolio> portfolio, MarketEnum stock, MarketEnum crypto) {
        return combineAssets(stock.assetsInfo.apply(this, portfolio), crypto.assetsInfo.apply(this, portfolio));
    }

    private List<Asset> combineAssets(List<? extends Asset> stockAssets, List<? extends Asset> cryptoAssets) {
        return Stream.concat(stockAssets.stream(), cryptoAssets.stream()).toList();
    }


    List<? extends Asset> getMarketstackAssets(List<Portfolio> e) {
        Optional<MarketstackService> marketstack = accessKey.map(MarketstackService::new);
        if (marketstack.isPresent()) {
            try {
                return marketstack.get().getAssetsInfo(e);
            } catch (RetrievingDataException exception) {
                printError("MarketStack.com");
            }
        }
        return Collections.emptyList();
    }

    public static void printError(String assetName) {
        if (errorOption) {
            System.out.println(Ansi.AUTO.string("@|bold,red Couldn't fetch information about + " + assetName + "!|@"));
        }
    }

    public Optional<String> getExchangeCurrency() {
        return exchangeCurrency;
    }

    public String getAccessKey() {
        if (accessKey.isPresent()) {
            return accessKey.get();
        } else {
            printError("Provide private access key");
            System.exit(2);
            return "";
        }
    }

}
