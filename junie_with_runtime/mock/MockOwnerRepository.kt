package org.springframework.samples.petclinic.mock

import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.stereotype.Component
import java.util.ArrayList
import java.util.HashMap
import java.util.NoSuchElementException

/**
 * Mock implementation of [OwnerRepository] that returns predefined data.
 * Based on the runtime information:
 * REPOSITORY CALL: OwnerRepository.findByLastName("sandler")
 * REPOSITORY RETURN: OwnerRepository.findByLastName -> [0 items]
 * REPOSITORY CALL: OwnerRepository.findByLastName("johnson")
 * REPOSITORY RETURN: OwnerRepository.findByLastName -> [0 items]
 * REPOSITORY CALL: OwnerRepository.save(org.springframework.samples.petclinic.owner.Owner@2b8ab6ff)
 * REPOSITORY RETURN: OwnerRepository.save -> null
 * REPOSITORY CALL: OwnerRepository.findById(11)
 * REPOSITORY RETURN: OwnerRepository.findById -> org.springframework.samples.petclinic.owner.Owner@f5e7b91
 * REPOSITORY CALL: OwnerRepository.findByLastName("Jackson")
 * REPOSITORY RETURN: OwnerRepository.findByLastName -> [1 items]
 */
@Component
class MockOwnerRepository : OwnerRepository {

    private val owners = HashMap<Int, Owner>()

    init {
        // Create a sample owner with ID 11
        val owner = Owner()
        owner.id = 11
        owner.firstName = "John"
        owner.lastName = "Jackson"
        owner.address = "123 Main St"
        owner.city = "New York"
        owner.telephone = "1234567890"

        owners.put(owner.id!!, owner)
    }

    override fun findByLastName(lastName: String): Collection<Owner> {
        val result = ArrayList<Owner>()

        // Based on runtime info, only "Jackson" returns results
        if (lastName.equals("Jackson")) {
            for (owner in owners.values) {
                if (owner.lastName.equals("Jackson")) {
                    result.add(owner)
                }
            }
        }

        return result
    }

    override fun findById(id: Int): Owner {
        return owners.get(id) ?: throw NoSuchElementException("Owner not found with ID: $id")
    }

    override fun save(owner: Owner) {
        // If the owner has an ID, update the existing owner
        // Otherwise, generate a new ID and add the owner
        if (owner.id == null) {
            owner.id = owners.size + 1
        }

        owners.put(owner.id!!, owner)
    }
}
