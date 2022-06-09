package ovh.miroslaw.investing;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;

import java.util.Optional;

public enum Constant {
    DEFAULT_EXCHANGE_CURRENCY("USD"),
    ARG_KEY("-k"),
    ARG_EXCHANGE_CURRENCY("-e"),
    ARG_ERROR("--no-errors"),
    ARG_SOUND("-s");

    public final String value;

    Constant(String value) {
        this.value = value;
    }


    public static Optional<OptionSpec> getCommandParameter(CommandSpec commandSpec,
            String paramName) {
        return commandSpec.options().stream()
                .filter(e -> e.shortestName().equals(paramName))
                .findAny();
    }
}
