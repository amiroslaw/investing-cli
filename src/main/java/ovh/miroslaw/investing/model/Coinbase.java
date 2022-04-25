package ovh.miroslaw.investing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Coinbase(String assetName,
        String symbol,
        @JsonProperty(value = "base") String assetSymbol,
        @JsonProperty(value = "currency") String exchangeCurrency,
        @JsonProperty(value = "amount") BigDecimal price) implements Asset {

    @Override
    public String toString() {
        return """
                %s-%s %s
                """.formatted(assetSymbol, exchangeCurrency, price);
    }
}

