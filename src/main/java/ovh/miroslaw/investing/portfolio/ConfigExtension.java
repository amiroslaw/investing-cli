package ovh.miroslaw.investing.portfolio;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigExtension {

    public enum configExt {
        YAML(new YAMLFactory()), JSON(new JsonFactory());
        private final JsonFactory factory;

        configExt(JsonFactory factory) {
            this.factory = factory;
        }

        public JsonFactory getFactory() {
            return factory;
        }
    }
}
