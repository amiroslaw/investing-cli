package ovh.miroslaw.investing.output;

import ovh.miroslaw.investing.model.Asset;

import java.util.List;

public class Notification implements OutputDecorator {

    private Output output;

    public Notification(Output output) {
        this.output = output;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        final String msg = output.display(assets);
        NotificationSender.notify(msg);
        return msg;
    }
}
