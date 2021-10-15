package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.model.PortfolioUtil;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ShortWithSymbol implements Output {

    private final BiFunction<List<? extends Asset>, Map<String, Portfolio>, String> shortOutputSymbol = (e, p) -> e
            .stream()
            .filter(a -> p.get(a.assetName()) != null)
            .map(a -> p.get(a.assetName()).assetSymbol() + " " + a.price().setScale(2, RoundingMode.DOWN))
            .collect(Collectors.joining("; "));

    private final List<Portfolio> portfolio;

    private final Output output;

    public ShortWithSymbol(List<Portfolio> portfolio, Output output) {
        this.output = output;
        this.portfolio = portfolio;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        final Map<String, Portfolio> portfolioMap = PortfolioUtil.convertPortfolioToSymbolMap(portfolio);

        String msg = output.display(assets);
        msg = msg.isBlank() ? "" : msg + System.lineSeparator();
        return msg + shortOutputSymbol.apply(assets, portfolioMap);
    }
}
