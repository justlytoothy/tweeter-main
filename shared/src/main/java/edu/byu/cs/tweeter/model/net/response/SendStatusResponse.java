package edu.byu.cs.tweeter.model.net.response;

public class SendStatusResponse extends Response {
    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public SendStatusResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     */
    public SendStatusResponse() {
        super(true, null);
    }
}
