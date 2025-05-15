package mate.academy.carsharing.controller.car;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.carsharing.dto.car.CarRequestDto;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.model.Car;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext webApplicationContext,
            @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
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
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Get all cars - returns list of cars with status 200")
    void getAll_ShouldReturnListOfCars_WhenRequested() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertNotNull(content);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Get car by ID - returns car details with status 200 for valid ID")
    void getById_ShouldReturnCar_WhenValidIdProvided() throws Exception {
        Long validCarId = 1L;

        MvcResult result = mockMvc.perform(get("/cars/{id}", validCarId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class);
        assertNotNull(actual);
        assertEquals(validCarId, actual.getId());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Create car - creates new car and returns 200 for valid request")
    void create_ShouldCreateCar_WhenValidRequest() throws Exception {
        CarRequestDto requestDto = new CarRequestDto()
                .setModel("Tesla Model S")
                .setBrand("Tesla")
                .setType(Car.Type.SEDAN)
                .setDailyFee(new BigDecimal("100.00"))
                .setInventory(5);

        MvcResult result = mockMvc.perform(post("/cars")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(requestDto.getModel(), actual.getModel());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @Sql(scripts = "classpath:database/add-tesla-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-tesla-car.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update car - updates existing car and returns 200 for valid ID")
    void update_ShouldUpdateCar_WhenValidIdProvided() throws Exception {
        Long existingCarId = 4L;
        CarRequestDto updateDto = new CarRequestDto()
                .setModel("Tesla Model X")
                .setBrand("Tesla")
                .setType(Car.Type.SUV)
                .setDailyFee(new BigDecimal("120.00"))
                .setInventory(3);

        MvcResult result = mockMvc.perform(put("/cars/{id}", existingCarId)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CarResponseDto.class);
        assertNotNull(actual);
        assertEquals(existingCarId, actual.getId());
        assertEquals(updateDto.getModel(), actual.getModel());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @Sql(scripts = "classpath:database/add-tesla-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete car - removes car and returns 204 for valid ID")
    void delete_ShouldRemoveCar_WhenValidIdProvided() throws Exception {
        Long validCarId = 4L;
        mockMvc.perform(delete("/cars/{id}", validCarId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Create car - returns 403 when unauthorized user tries to create")
    void create_ShouldReturnForbidden_WhenUnauthorized() throws Exception {
        CarRequestDto requestDto = new CarRequestDto()
                .setModel("Tesla Model S")
                .setBrand("Tesla")
                .setType(Car.Type.SEDAN)
                .setDailyFee(new BigDecimal("100.00"))
                .setInventory(5);

        mockMvc.perform(post("/cars")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
