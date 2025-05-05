package mate.academy.carsharing.controller.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.dto.rental.RentalReturnRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/create-test-for-rental.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all.sql")
            );
        }
    }

    @Test
    @WithUserDetails(value = "user@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @DisplayName("Create rental - returns created rental with status 201")
    void createRental_ShouldReturnCreatedRental_WhenValidRequest() throws Exception {
        RentalRequestDto requestDto = new RentalRequestDto()
                .setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(7))
                .setCarId(1L);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        RentalResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class);
        assertNotNull(response);
        assertEquals(requestDto.getCarId(), response.getCarId());
        assertEquals(requestDto.getRentalDate(), response.getRentalDate());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "CUSTOMER")
    @DisplayName("Complete rental - returns updated rental with status 200")
    void completeRental_ShouldReturnUpdatedRental_WhenValidRequest() throws Exception {
        RentalReturnRequestDto requestDto = new RentalReturnRequestDto()
                .setRentalId(1L)
                .setActualReturnDate(LocalDate.now());

        mockMvc.perform(post("/rentals/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Create rental - returns 403 when manager tries to create")
    void createRental_ShouldReturnForbidden_WhenManagerRole() throws Exception {
        RentalRequestDto requestDto = new RentalRequestDto()
                .setCarId(1L)
                .setRentalDate(LocalDate.now().plusDays(1))
                .setReturnDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Get rentals - returns 403 when customer tries to get all")
    void getAllByUserId_ShouldReturnForbidden_WhenCustomerRole() throws Exception {
        mockMvc.perform(get("/rentals")
                        .param("user_id", "1")
                        .param("is_active", "true"))
                .andExpect(status().isForbidden());
    }
}
