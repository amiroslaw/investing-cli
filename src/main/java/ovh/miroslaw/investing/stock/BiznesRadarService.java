package ovh.miroslaw.investing.stock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ovh.miroslaw.investing.model.BiznesRadar;
import ovh.miroslaw.investing.model.Portfolio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ovh.miroslaw.investing.model.AssetType.GPW;

public class BiznesRadarService {

    public static final String API = "https://www.biznesradar.pl/notowania/";

    public List<BiznesRadar> getAssetsInfo(List<Portfolio> portfolio) {
        return portfolio.stream()
                .filter(e -> GPW.equals(e.type()))
                .map(this::getAssetInfo)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<BiznesRadar> getAssetInfo(Portfolio portfolioAsset) {
        Document doc = null;
        try {
            doc = Jsoup.connect(API + portfolioAsset.assetName()).get();
        } catch (Exception e) {
            MarketFactory.printError(portfolioAsset.assetName());
            return Optional.empty();
        }
        final Element profileCurrent = doc.getElementById("profileSummaryCurrent");
        final Element price = profileCurrent.getElementsByClass("current").first().select("td").first();
        final Element minPrice = profileCurrent.getElementById("pr_t_min");
        final Element maxPrice = profileCurrent.getElementById("pr_t_max");

        final Element profileCompare = doc.getElementById("profileSummaryCompare");
        final String priceChangePercent = profileCompare.getElementsByClass("q_ch_per").first().text();

        final Elements ratings = doc.select("div.ratings");
        final String rating = ratings.select("td").get(1).text();

        final Element indicators = doc.getElementById("profile-indicators");
        final String atIndicator = indicators.getElementsByClass("indicator-result").first().text();

        final Elements trends = doc.select("table.trends");
        final String trend = trends.select("td").get(2).text();

        return Optional.of(new BiznesRadar(portfolioAsset.assetName(), convertToBigDecimal(price),
                convertToBigDecimal(minPrice),
                convertToBigDecimal(maxPrice),
                priceChangePercent, rating, atIndicator, trend));
    }

    private BigDecimal convertToBigDecimal(Element price) {
        if (Objects.nonNull(price) && price.hasText()) {
            try {
                return new BigDecimal(price.text());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
