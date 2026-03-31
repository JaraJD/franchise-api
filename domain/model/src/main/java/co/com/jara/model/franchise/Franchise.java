package co.com.jara.model.franchise;

import co.com.jara.model.branch.Branch;
import lombok.Builder;
import lombok.With;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@With
public record Franchise(
        String id,
        String name,
        List<Branch> branches
) {
    public Franchise addBranch(Branch branch) {
        List<Branch> updated = new ArrayList<>(branches);
        updated.add(branch);
        return this.toBuilder().branches(updated).build();
    }

    public Franchise updateBranch(Branch updatedBranch) {
        List<Branch> updated = branches.stream()
                .map(b -> b.id().equals(updatedBranch.id()) ? updatedBranch : b)
                .toList();
        return this.toBuilder().branches(updated).build();
    }

    public Franchise updateName(String newName) {
        return this.toBuilder().name(newName).build();
    }

    public java.util.Optional<Branch> findBranch(String branchId) {
        return branches.stream()
                .filter(b -> b.id().equals(branchId))
                .findFirst();
    }
}
