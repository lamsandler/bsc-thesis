package org.springframework.samples.petclinic.mock

import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetRepository
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.ArrayList
import java.util.HashMap
import java.util.NoSuchElementException

/**
 * Mock implementation of [PetRepository] that returns predefined data.
 * Based on the runtime information:
 * REPOSITORY CALL: PetRepository.findPetTypes()
 * REPOSITORY RETURN: PetRepository.findPetTypes -> [6 items]
 * REPOSITORY CALL: PetRepository.findById(14)
 * REPOSITORY RETURN: PetRepository.findById -> Шарик
 * REPOSITORY CALL: PetRepository.save(Шарик)
 * REPOSITORY RETURN: PetRepository.save -> null
 */
@Component
class MockPetRepository : PetRepository {

    private val petTypes = ArrayList<PetType>()
    private val pets = HashMap<Int, Pet>()

    init {
        // Create 6 pet types
        val petTypeNames = arrayOf("cat", "dog", "lizard", "snake", "bird", "hamster")
        for (i in 1..6) {
            val petType = PetType()
            petType.id = i
            petType.name = petTypeNames[i - 1]
            petTypes.add(petType)
        }

        // Create a pet with ID 14 named "Шарик"
        val pet = Pet()
        pet.id = 14
        pet.name = "Шарик"
        pet.birthDate = LocalDate.now().minusYears(2)
        pet.type = petTypes.get(1) // dog
        pets.put(pet.id!!, pet)
    }

    override fun findPetTypes(): List<PetType> {
        return petTypes
    }

    override fun findById(id: Int): Pet {
        return pets.get(id) ?: throw NoSuchElementException("Pet not found with ID: $id")
    }

    override fun save(pet: Pet) {
        // If the pet has an ID, update the existing pet
        // Otherwise, generate a new ID and add the pet
        if (pet.id == null) {
            pet.id = pets.size + 1
        }

        pets.put(pet.id!!, pet)
    }
}
