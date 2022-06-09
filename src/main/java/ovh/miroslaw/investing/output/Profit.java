package ovh.miroslaw.investing.output;

import org.jooq.lambda.tuple.Tuple2;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.portfolio.ProfitChecker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Profit implements Output {

    private final Output output;
    private final List<Portfolio> portfolio;
    private final ProfitChecker profitChecker;

    public Profit(List<Portfolio> portfolio, String exchangeCurrency, Output output) {
        this.output = output;
        this.portfolio = portfolio;
        this.profitChecker = new ProfitChecker(exchangeCurrency);
    }

    @Override
    public String display(List<? extends Asset> assets) {
        String msg = output.display(assets);
        msg += System.lineSeparator() + getMsg(profitChecker.checkPortfolio(portfolio, assets));
        return msg;
    }

    public String getMsg(Map<Portfolio, Tuple2<BigDecimal, BigDecimal>> portfolio) {
        final String portfolioAssets = portfolio.entrySet().stream()
                .map(e -> e.getKey().assetSymbol() + " " + getProfitOrRevenue(e.getValue()))
                .collect(Collectors.joining("\n"));

        final String profit = profitChecker.getProfitSum(portfolio)
                .map(e -> "Profit summary: " + e.setScale(2, RoundingMode.DOWN))
                .orElse("");
        final String revenue = profitChecker.getRevenueSum(portfolio)
                .map(e -> "Revenue summary: " + e.setScale(2, RoundingMode.DOWN))
                .orElse("");
        return portfolioAssets + System.lineSeparator() + profit + System.lineSeparator() + revenue;
    }

    private String getProfitOrRevenue(Tuple2<BigDecimal, BigDecimal> profitAndRevenue) {
        final boolean isProfitEqualZero = profitAndRevenue.v1.equals(BigDecimal.ZERO);
        if (isProfitEqualZero) {
            return " revenue: " + profitAndRevenue.v2.setScale(2, RoundingMode.DOWN);
        }
        return " profit: " + profitAndRevenue.v1.setScale(2, RoundingMode.DOWN);
    }
}
