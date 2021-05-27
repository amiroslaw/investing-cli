package ovh.miroslaw.investing.model;

public enum AssetType {
    CRYPTO("crypto"), GPW("gpw"), STOCK("stock");

    private final String type;

    private AssetType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
