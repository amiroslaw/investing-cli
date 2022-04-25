package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.PortfolioUtil;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class ShortWithSymbol implements Output {

    private final Output output;

    public ShortWithSymbol(Output output) {
        this.output = output;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        final String shortMsg = assets.stream()
                .map(a -> PortfolioUtil.splitSymbolFromMarket(a.symbol()) + " " + a.price()
                        .setScale(2, RoundingMode.DOWN))
                .collect(Collectors.joining("; "));

        String msg = output.display(assets);
        msg = msg.isBlank() ? "" : msg + System.lineSeparator();
        return msg + shortMsg;
    }
}
