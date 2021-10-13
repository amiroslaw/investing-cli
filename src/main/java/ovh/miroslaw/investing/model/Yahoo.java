package ovh.miroslaw.investing.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Yahoo(String assetName,
                    BigDecimal price,
                    BigDecimal minPrice,
                    BigDecimal maxPrice,
                    BigDecimal priceChangePercent,
                    String fiftyTwoWeekRange) implements Asset {

    @Override
    public String toString() {
        return """
                %s %s %s%%
                   day range %s - %s
                   year range %s
                """.formatted(assetName,
                Objects.requireNonNullElse(price, "#"),
                priceChangePercent,
                Objects.requireNonNullElse(minPrice, "#"),
                Objects.requireNonNullElse(maxPrice, "#"),
                fiftyTwoWeekRange
        );

    }
}
