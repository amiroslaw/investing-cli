package ovh.miroslaw.investing.model;

import java.math.BigDecimal;

public interface Asset {

    public String assetName();

//    String assetSymbol,
//    public String assetSymbol();

    public BigDecimal price();
}
