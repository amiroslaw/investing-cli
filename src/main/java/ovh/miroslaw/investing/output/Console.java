package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.util.List;

public class Console implements OutputDecorator {

    private Output output;

    public Console(Output output) {
        this.output = output;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        final String msg = output.display(assets);
        System.out.println(msg);
        return msg;
    }
}
