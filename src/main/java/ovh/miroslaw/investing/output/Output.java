package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.util.List;

public abstract class Output {
    public String display(List<? extends Asset> assets){return "";}
}
