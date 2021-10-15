package ovh.miroslaw.investing.model;

public enum AssetType {
    CRYPTO("crypto"), GPW("gpw"), STOCK("stock"), CC("crypto-crypto");

    private final String type;

    AssetType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
