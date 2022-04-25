package ovh.miroslaw.investing.model;

import java.math.BigDecimal;

public interface Asset {

    public String assetName();
    public BigDecimal price();
    public String symbol();

}
