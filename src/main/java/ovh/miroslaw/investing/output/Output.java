package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.util.List;

public interface Output {
    String display(List<? extends Asset> assets);
}
