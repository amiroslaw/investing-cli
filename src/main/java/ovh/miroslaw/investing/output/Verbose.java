package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Verbose implements OutputDecorator {

    private final Function<List<? extends Asset>, String> verboseOutput = e -> e.stream()
            .map(Objects::toString)
            .collect(Collectors.joining(""));

    private final Output output;

    public Verbose(Output output) {
        this.output = output;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        String msg = output.display(assets);
        msg = msg.isBlank() ? "" : msg + System.lineSeparator();
        return msg + verboseOutput.apply(assets);
    }
}
