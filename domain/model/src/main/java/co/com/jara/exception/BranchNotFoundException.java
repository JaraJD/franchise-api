package co.com.jara.exception;

public class BranchNotFoundException extends DomainException {

    public BranchNotFoundException(String branchId) {
        super("BRANCH_NOT_FOUND", "Branch not found with id: " + branchId);
    }
}
