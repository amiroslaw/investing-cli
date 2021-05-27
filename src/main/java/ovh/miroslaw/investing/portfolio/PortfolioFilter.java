package ovh.miroslaw.investing.portfolio;

import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.RetrievingDataException.ErrorCode;
import ovh.miroslaw.investing.model.AssetType;
import ovh.miroslaw.investing.model.Portfolio;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ovh.miroslaw.investing.model.AssetType.CRYPTO;
import static ovh.miroslaw.investing.model.AssetType.GPW;

public class PortfolioFilter {

    List<Portfolio> portfolio;

    public PortfolioFilter(List<Portfolio> portfolio) {
        this.portfolio = portfolio;
    }

    public List<Portfolio> applyFilter(Optional<String> typeOption, Optional<String> onlyAssetsOption)
            throws RetrievingDataException {

        Predicate<Portfolio> typeFilter = convertTypeOptionToPredicate(typeOption);

        final Predicate<Portfolio> assetsFilter = onlyAssetsOption.map(this::convertOptionsToList)
                .map(this::getAssetFilter)
                .orElse(e -> true);
        return applyFilter(portfolio, typeFilter, assetsFilter);
    }

    private Predicate<Portfolio> convertTypeOptionToPredicate(Optional<String> typeOption) throws RetrievingDataException {
        Predicate<Portfolio> typeFilter;
        try {
            typeFilter = typeOption.map(AssetType::valueOf)
                    .map(this::getFilterType)
                    .orElse(e -> true);
        } catch (IllegalArgumentException e) {
            throw new RetrievingDataException("--type parameter is invalid!", ErrorCode.USAGE);
        }
        return typeFilter;
    }

    private List<String> convertOptionsToList(String onlyOption) {
        return Arrays.stream(onlyOption.split(","))
                .map(String::toUpperCase)
                .map(e -> e.replace(" ", ""))
                .toList();
    }

    private List<Portfolio> applyFilter(List<Portfolio> portfolio, Predicate<Portfolio> typeFilter,
            Predicate<Portfolio> assetFilter) {
        return portfolio.stream()
                .filter(typeFilter)
                .filter(assetFilter)
                .toList();
    }

    private Predicate<Portfolio> getFilterType(AssetType type) {
        return switch (type) {
            case CRYPTO -> e -> CRYPTO.equals(e.type());
            case GPW -> e -> GPW.equals(e.type());
            case STOCK -> e -> true;
        };
    }

    private Predicate<Portfolio> getAssetFilter(List<String> onlyAssets) {
        if (onlyAssets.isEmpty()) {
            return e -> true;
        } else {
            return e -> onlyAssets.contains(e.assetSymbol());
        }
    }
}
