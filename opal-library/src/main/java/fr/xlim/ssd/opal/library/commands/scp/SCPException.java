package fr.xlim.ssd.opal.library.commands.scp;

public class SCPException extends Exception {

    public SCPException() {
    }

    public SCPException(String s) {
        super(s);
    }

    public SCPException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SCPException(Throwable throwable) {
        super(throwable);
    }
}
