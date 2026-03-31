package co.com.jara.exception;

public class DuplicateNameException extends DomainException {

    public DuplicateNameException(String entity, String name) {
        super("DUPLICATE_NAME", entity + " already exists with name: " + name);
    }
}
