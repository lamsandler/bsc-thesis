package org.springframework.samples.petclinic.owner

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.samples.petclinic.mock.MockOwnerRepository
import org.springframework.samples.petclinic.mock.MockVisitRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * Test class for the [OwnerController]
 */
class OwnerControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var ownerRepository: MockOwnerRepository
    private lateinit var visitRepository: MockVisitRepository
    private lateinit var ownerController: OwnerController

    @BeforeEach
    fun setup() {
        ownerRepository = MockOwnerRepository()
        visitRepository = MockVisitRepository()
        ownerController = OwnerController(ownerRepository, visitRepository)
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build()
    }

    @Test
    fun testInitFindForm() {
        mockMvc.perform(get("/owners/find"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/findOwners"))
    }

    @Test
    fun testProcessFindFormNoResults() {
        mockMvc.perform(get("/owners")
            .param("lastName", "sandler"))
            .andExpect(status().isOk)
            .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
            .andExpect(view().name("owners/findOwners"))
    }

    @Test
    fun testProcessFindFormOneResult() {
        mockMvc.perform(get("/owners")
            .param("lastName", "Jackson"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/11"))
    }

    @Test
    fun testShowOwner() {
        mockMvc.perform(get("/owners/11"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/ownerDetails"))
    }

    @Test
    fun testInitCreationForm() {
        mockMvc.perform(get("/owners/new"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    @Test
    fun testRepositoryFindByLastName() {
        // Test that the repository returns 0 owners for "sandler" as specified in the runtime information
        val sandlerResults = ownerRepository.findByLastName("sandler")
        assertEquals(0, sandlerResults.size, "Expected 0 owners for 'sandler', but got ${sandlerResults.size}")

        // Test that the repository returns 0 owners for "johnson" as specified in the runtime information
        val johnsonResults = ownerRepository.findByLastName("johnson")
        assertEquals(0, johnsonResults.size, "Expected 0 owners for 'johnson', but got ${johnsonResults.size}")

        // Test that the repository returns 1 owner for "Jackson" as specified in the runtime information
        val jacksonResults = ownerRepository.findByLastName("Jackson")
        assertEquals(1, jacksonResults.size, "Expected 1 owner for 'Jackson', but got ${jacksonResults.size}")
    }

    @Test
    fun testRepositoryFindById() {
        // Test that the repository returns an owner with ID 11 as specified in the runtime information
        val owner = ownerRepository.findById(11)
        assertEquals(11, owner.id, "Expected owner with ID 11, but got ${owner.id}")
        assertEquals("Jackson", owner.lastName, "Expected owner with lastName 'Jackson', but got ${owner.lastName}")
    }
}
