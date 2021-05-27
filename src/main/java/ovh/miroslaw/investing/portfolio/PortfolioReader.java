package ovh.miroslaw.investing.portfolio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.RetrievingDataException.ErrorCode;
import ovh.miroslaw.investing.model.Portfolio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PortfolioReader {

    private PortfolioReader() {
    }

    public static final String PORTFOLIO_FILE_NAME = "portfolio.json";
    public static final String CONFIG_FILE_NAME = "investing";

    public static List<Portfolio> getPortfolio(File portfolioConfig) throws RetrievingDataException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(portfolioConfig, new TypeReference<List<Portfolio>>() {
            });
        } catch (IOException e) {
            final String errorMsg = "Error with reading portfolio file from: " + portfolioConfig.getAbsolutePath();
            throw new RetrievingDataException(errorMsg, ErrorCode.SOFTWARE);
        }
    }

    public static Path getConfigPath() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        final Map<String, String> environment = processBuilder.environment();
        final Optional<String> xdgConfigHome = Optional.of(environment.get("XDG_CONFIG_HOME"));
        final String home = environment.get("HOME");
        return Path.of(xdgConfigHome.map(e -> e + "/" + CONFIG_FILE_NAME)
                .orElse(home + ".") + "/" + PORTFOLIO_FILE_NAME);
    }
}
