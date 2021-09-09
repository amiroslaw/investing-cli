package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Short extends Output {

    public static final Function<List<? extends Asset>, String> shortOutput = e -> e.stream()
            .map(a -> a.assetName() + " " + a.price().setScale(2, RoundingMode.DOWN))
            .collect(Collectors.joining("; "));

    @Override
    public String display(List<? extends Asset> assets) {
        return shortOutput.apply(assets);
    }
}
