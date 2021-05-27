package ovh.miroslaw.investing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Portfolio(String assetName, String assetSymbol, AssetType type, List<Holding> holdings,
                        List<Alert> alerts) {

    @Override
    public List<Alert> alerts() {
        if (alerts == null) {
            return Collections.emptyList();
        }
        return alerts;
    }

    @Override
    public List<Holding> holdings() {
        if (holdings == null) {
            return Collections.emptyList();
        }
        return holdings;
    }

    public record Alert(BigDecimal price, AlertCondition alertCondition) {

    }

    public record Holding(BigDecimal buyPrice, BigDecimal sellPrice, Double amount) {

    }
}

