package org.springframework.samples.petclinic.vet

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.samples.petclinic.mock.MockVetRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * Test class for the [VetController]
 */
class VetControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var vetRepository: MockVetRepository
    private lateinit var vetController: VetController

    @BeforeEach
    fun setup() {
        vetRepository = MockVetRepository()
        vetController = VetController(vetRepository)
        mockMvc = MockMvcBuilders.standaloneSetup(vetController).build()
    }

    @Test
    fun testShowVetListHtml() {
        mockMvc.perform(get("/vets.html"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("vets"))
            .andExpect(view().name("vets/vetList"))
    }

    @Test
    fun testShowVetListJson() {
        mockMvc.perform(get("/vets.json"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.vetList").isArray)
            .andExpect(jsonPath("$.vetList.length()").value(6))
    }

    @Test
    fun testShowVetListXml() {
        mockMvc.perform(get("/vets.xml"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/xml"))
    }

    @Test
    fun testRepositoryFindAll() {
        // Test that the repository returns 6 vets as specified in the runtime information
        val vets = vetRepository.findAll()
        assertEquals(6, vets.size, "Expected 6 vets, but got ${vets.size}")
    }
}
