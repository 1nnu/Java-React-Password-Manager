package ee.taltech.passman.exceptions;

public class ValidationException extends RuntimeException {

  public ValidationException(String errorMessage) {
    super(errorMessage);
  }
}
