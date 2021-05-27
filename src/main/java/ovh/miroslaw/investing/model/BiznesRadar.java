package ovh.miroslaw.investing.model;

import java.math.BigDecimal;
import java.util.Objects;

public record BiznesRadar(String assetName,
                          BigDecimal price,
                          BigDecimal minPrice,
                          BigDecimal maxPrice,
                          String priceChangePercent,
                          String rating,
                          String atIndicator,
                          String trend) implements Asset {

    @Override
    public String rating() {
        return Objects.requireNonNullElse(rating, "#");
    }

    @Override
    public String atIndicator() {
        return Objects.requireNonNullElse(atIndicator, "#");
    }

    @Override
    public String trend() {
        return Objects.requireNonNullElse(trend, "#");
    }

    @Override
    public String priceChangePercent() {

        return Objects.requireNonNullElse(priceChangePercent, "#");
    }

    @Override
    public String toString() {
        return """
                %s %s %s
                    %s - %s %s
                    indicator: %s; rating: %s
                """.formatted(assetName,
                Objects.requireNonNullElse(price, "#"),
                priceChangePercent,
                Objects.requireNonNullElse(minPrice, "#"),
                Objects.requireNonNullElse(maxPrice, "#"),
                trend, atIndicator, rating);
    }
}
