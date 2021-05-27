package ovh.miroslaw.investing.portfolio;

import org.jooq.lambda.tuple.Tuple2;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.model.Portfolio.Holding;
import ovh.miroslaw.investing.model.PortfolioUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.lambda.tuple.Tuple.tuple;

public class ProfitChecker {

    private ProfitChecker() {
    }

    public static Map<Portfolio, Tuple2<BigDecimal, BigDecimal>> checkPortfolio(List<Portfolio> portfolio,
            List<? extends Asset> assets) {
        final Map<String, BigDecimal> assetsMap = PortfolioUtil.convertAssetsToNamePriceMap(assets);

        return portfolio.stream()
                .filter(e -> !e.holdings().isEmpty())
                .filter(e -> assetsMap.get(e.assetName()) != null)
                .collect(Collectors.toMap(e -> e, e -> calculateProfitAndRevenue(e, assetsMap.get(e.assetName()))));
    }

    public static Optional<BigDecimal> getRevenueSum(Map<Portfolio, Tuple2<BigDecimal, BigDecimal>> portfolioMap) {
        BigDecimal sum = portfolioMap.values().stream()
                .map(e -> e.v2)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return wrapWithOptional(sum);
    }

    public static Optional<BigDecimal> getProfitSum(Map<Portfolio, Tuple2<BigDecimal, BigDecimal>> portfolioMap) {
        BigDecimal sum = portfolioMap.values().stream()
                .map(e -> e.v1)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return wrapWithOptional(sum);
    }

    private static Optional<BigDecimal> wrapWithOptional(BigDecimal sum) {
        if (sum.equals(BigDecimal.ZERO)) {
            return Optional.empty();
        } else {
            return Optional.of(sum);
        }
    }

    private static Tuple2<BigDecimal, BigDecimal> calculateProfitAndRevenue(Portfolio portfolio,
            BigDecimal currentPrice) {
        BigDecimal profitSum = BigDecimal.ZERO;
        BigDecimal revenueSum = BigDecimal.ZERO;
        for (Holding holding : portfolio.holdings()) {
            if (holding.buyPrice() != null) {
                final BigDecimal profit = calculateProfit(currentPrice, holding.buyPrice(), holding.amount());
                profitSum = profitSum.add(profit);
            }
            if (holding.sellPrice() != null) {
                final BigDecimal profit = calculateProfit(holding.sellPrice(), currentPrice, holding.amount());
                profitSum = profitSum.add(profit);
            }

            final BigDecimal revenue = calculateRevenue(currentPrice, holding.amount());
            revenueSum = revenueSum.add(revenue);
        }
        return tuple(profitSum, revenueSum);
    }

    private static BigDecimal calculateRevenue(BigDecimal currentPrice, Double amount) {
        return currentPrice.multiply(BigDecimal.valueOf(amount));
    }

    private static BigDecimal calculateProfit(BigDecimal minuend, BigDecimal subtrahend, Double amount) {
        return minuend.subtract(subtrahend)
                .multiply(BigDecimal.valueOf(amount));
    }
}
