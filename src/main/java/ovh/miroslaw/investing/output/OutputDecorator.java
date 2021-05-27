package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.util.List;

public abstract class OutputDecorator implements Output {

    @Override
    public abstract String display(List<? extends Asset> assets);
}
