package ovh.miroslaw.investing;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class PrintExceptionMessageHandler implements IExecutionExceptionHandler {

    private boolean printError;

    public PrintExceptionMessageHandler(boolean printError) {
        this.printError = printError;
    }

    public int handleExecutionException(Exception ex, CommandLine cmd, ParseResult parseResult) {

        if (printError) {
            cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));
        }
        if (ex instanceof RetrievingDataException) {
            return ((RetrievingDataException) ex).getErrorCode().code;
        }

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
