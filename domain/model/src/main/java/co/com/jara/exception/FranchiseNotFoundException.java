package co.com.jara.exception;

public class FranchiseNotFoundException extends DomainException {

    public FranchiseNotFoundException(String franchiseId) {
        super("FRANCHISE_NOT_FOUND", "Franchise not found with id: " + franchiseId);
    }
}
