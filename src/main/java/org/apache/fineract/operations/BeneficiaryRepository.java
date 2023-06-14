package org.apache.fineract.operations;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BeneficiaryRepository extends CrudRepository<Beneficiary, Long> {

    List<Beneficiary> findBycustIdentifier(String custIdentifier);

    Beneficiary findOneByCustIdentifierAndIdentifier(String custIdentifier, String identifier);
}
