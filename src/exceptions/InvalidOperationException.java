package exceptions;

public class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message); // Pass the message to the parent Exception class
    }
}
