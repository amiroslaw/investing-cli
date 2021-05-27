package ovh.miroslaw.investing.stock;

import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class MarketFactory {

    private final CommandSpec commandSpec;

    private static Optional<String> exchangeCurrency;
    private static Optional<String> accessKey;
    private static boolean errorOption;

    public MarketFactory(CommandSpec commandSpec) {
        this.commandSpec = commandSpec;
        exchangeCurrency = getCommandParameter("-e").flatMap(ArgSpec::getValue);
        accessKey = getCommandParameter("-k").flatMap(ArgSpec::getValue);
        errorOption = getCommandParameter("--no-errors")
                .map(e -> (Boolean)e.getValue())
                .orElse(true);
    }

    public List<Asset> getAssets(List<Portfolio> portfolio, Market market) {
        return combineAssets(Market.COINBASE.assetsInfo.apply(portfolio), market.assetsInfo.apply(portfolio));
    }

    public List<Asset> getAssets(List<Portfolio> portfolio, Market stock, Market crypto) {
        return combineAssets(stock.assetsInfo.apply(portfolio), crypto.assetsInfo.apply(portfolio));
    }

    public static void printError(String assetName) {
        if (errorOption) {
            System.out.println(
                    Ansi.AUTO.string("@|bold,red Couldn't fetch information about + " + assetName + "!|@"));
        }
    }

    private List<Asset> combineAssets(List<? extends Asset> stockAssets, List<? extends Asset> cryptoAssets) {
        return Stream.concat(stockAssets.stream(), cryptoAssets.stream()).toList();
    }

    private Optional<OptionSpec> getCommandParameter(String paramName) {
        return commandSpec.options().stream()
                .filter(e -> e.shortestName().equals(paramName))
                .findAny();
    }

    private static List<? extends Asset> getMarketstackAssets(List<Portfolio> e) {
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

    public enum Market {
        COINBASE(e -> exchangeCurrency
                .map(CoinbaseService::new)
                .map(c -> c.getAssetsInfo(e))
                .orElse(new CoinbaseService().getAssetsInfo(e))
        ),
        BIZ(e -> new BiznesRadarService().getAssetsInfo(e)),
        MARKETSTACK(MarketFactory::getMarketstackAssets);

        private final Function<List<Portfolio>, List<? extends Asset>> assetsInfo;

        Market(Function<List<Portfolio>, List<? extends Asset>> assetsInfo) {
            this.assetsInfo = assetsInfo;
        }
    }
}
