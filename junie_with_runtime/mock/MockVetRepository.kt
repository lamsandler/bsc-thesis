package org.springframework.samples.petclinic.mock

import org.springframework.samples.petclinic.vet.Specialty
import org.springframework.samples.petclinic.vet.Vet
import org.springframework.samples.petclinic.vet.VetRepository
import org.springframework.stereotype.Component
import java.util.ArrayList

/**
 * Mock implementation of [VetRepository] that returns predefined data.
 * Based on the runtime information:
 * REPOSITORY CALL: VetRepository.findAll()
 * REPOSITORY RETURN: VetRepository.findAll -> [6 items]
 */
@Component
class MockVetRepository : VetRepository {

    private val vets = ArrayList<Vet>()

    init {
        // Create 6 vets with sample data
        for (i in 1..6) {
            val vet = Vet()
            vet.id = i
            vet.firstName = "Vet"
            vet.lastName = "Number $i"

            // Add some specialties to each vet
            if (i % 2 == 0) {
                val specialty1 = Specialty()
                specialty1.id = 1
                specialty1.name = "Radiology"
                vet.addSpecialty(specialty1)
            }

            if (i % 3 == 0) {
                val specialty2 = Specialty()
                specialty2.id = 2
                specialty2.name = "Surgery"
                vet.addSpecialty(specialty2)
            }

            vets.add(vet)
        }
    }

    override fun findAll(): Collection<Vet> {
        return vets
    }
}
