package org.springframework.samples.petclinic.mock

import org.springframework.samples.petclinic.visit.Visit
import org.springframework.samples.petclinic.visit.VisitRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

/**
 * Mock implementation of [VisitRepository] that returns predefined data.
 * Based on the runtime information:
 * REPOSITORY CALL: VisitRepository.findByPetId(14)
 * REPOSITORY RETURN: VisitRepository.findByPetId -> [0 items]
 * REPOSITORY CALL: VisitRepository.save(org.springframework.samples.petclinic.visit.Visit@33fffcb8)
 * REPOSITORY RETURN: VisitRepository.save -> null
 * REPOSITORY CALL: VisitRepository.findByPetId(14)
 * REPOSITORY RETURN: VisitRepository.findByPetId -> [1 items]
 */
@Component
class MockVisitRepository : VisitRepository {

    private val visits = HashMap<Int, MutableSet<Visit>>()
    private var nextId = 1

    override fun save(visit: Visit) {
        // If the visit has no ID, assign a new one
        if (visit.id == null) {
            visit.id = nextId++
        }

        // Get or create the set of visits for this pet
        val petVisits = visits.getOrDefault(visit.petId!!, HashSet())

        // Add the visit to the set
        petVisits.add(visit)

        // Update the map
        visits.put(visit.petId!!, petVisits)
    }

    override fun findByPetId(petId: Int): MutableSet<Visit> {
        return visits.getOrDefault(petId, HashSet())
    }

    // Helper method to add a visit for testing
    fun addVisit(petId: Int, description: String) {
        val visit = Visit()
        visit.id = nextId++
        visit.petId = petId
        visit.description = description
        visit.date = LocalDate.now()

        save(visit)
    }
}
