package ovh.miroslaw.investing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Marketstack(
        @JsonProperty(value = "base") String assetName,
        @JsonProperty(value = "amount") BigDecimal price,
        BigDecimal open, BigDecimal high, BigDecimal low
) implements Asset {

}

