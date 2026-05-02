package pl.krystianbeduch.gymmembership;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@AutoConfigureMockMvc
@ActiveProfiles("test")
class MembershipFlowE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

//    @Autowired
//    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    private Long createdGymId;
    private String createdGymName;
    private Long createdMembershipPlanId;

    @BeforeAll
    void cleanDb() {
        gymRepository.deleteAll();
        membershipPlanRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api")
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        String.valueOf(MediaType.APPLICATION_JSON))
                .build();
    }

    /// GymController
    @Test
    @Order(1)
    void createGym_shouldCreateGym() {
        String uniqueName = "Gym-" + UUID.randomUUID();
        GymCreateRequestDto request = GymTestDataFactory.createRequestDto(uniqueName);

        ResponseEntity<GymResponseDto> response = restClient.post()
                .uri("/gyms")
                .body(request)
                .retrieve()
                .toEntity(GymResponseDto.class);

        GymResponseDto gymResponse = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(gymResponse);
        assertNotNull(gymResponse.id());
        assertEquals(uniqueName, gymResponse.name());

        createdGymId = gymResponse.id();
        createdGymName = gymResponse.name();
    }

    @Test
    @Order(2)
    void getAllGyms_shouldGetGymsAndContainCreatedGym() {
        ResponseEntity<List<GymResponseDto>> response = restClient.get()
                .uri("/gyms")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        List<GymResponseDto> gyms = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(gyms);
        assertTrue(gyms.stream()
                .anyMatch(g -> g.id().equals(createdGymId))
        );
    }

    /// MembershipPlanController
    @Test
    @Order(3)
    void createMembershipPlan_shouldCreateMembershipPlanForCreatedGym() {
        MembershipPlanCreateRequestDto request = MembershipPlanTestDataFactory.createRequestDto();

        ResponseEntity<MembershipPlanResponseDto> response = restClient.post()
                .uri("/gyms/{gymId}/membership-plans", createdGymId)
                .body(request)
                .retrieve()
                .toEntity(MembershipPlanResponseDto.class);

        MembershipPlanResponseDto body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals(createdGymId, body.gymId());
        assertEquals(createdGymName, body.gymName());
        assertEquals("Premium 6M", body.name());
        assertEquals(MembershipPlanType.PREMIUM, body.type());
        assertEquals(new BigDecimal("999.99"), body.monthlyPriceAmount());
        assertEquals("PLN", body.monthlyPriceCurrencyCode());
        assertEquals(6, body.durationInMonths());
        assertEquals(100, body.maxMembers());

        createdMembershipPlanId = body.id();
    }

    @Test
    @Order(4)
    void getAllMembershipPlanForGym_shouldGetMembershipPlansForCreatedGym() {
        ResponseEntity<List<MembershipPlanResponseDto>> response = restClient.get()
                .uri("/gyms/{gymId}/membership-plans", createdGymId)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<MembershipPlanResponseDto> membershipPlans = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(membershipPlans);
        assertTrue(membershipPlans.stream()
                .anyMatch(plan -> plan.id().equals(createdMembershipPlanId)
                        && plan.gymId().equals(createdGymId)
                        && plan.name().equals("Premium 6M")));
    }

    /// Bad requests - GymController
    @Test
    @Order(5)
    void getAllMembershipPlansForGym_shouldReturnBadRequestWhenGymCreateRequestHasBlankName() {
        GymCreateRequestDto request = GymTestDataFactory.createRequestDto("  ");

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms")
                        .body(request)
                        .retrieve()
                        .toEntity(GymResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().contains("blank")
                || exception.getResponseBodyAsString().toLowerCase().contains("must not be blank"));
    }

    /// Bad requests - MembershipPlanController
    @Test
    @Order(6)
    void createMembershipPlan_shouldReturnBadRequestWhenMembershipPlanRequestHasBlankName() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "  ",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms/{gymId}/membership-plans", createdGymId)
                        .body(request)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().contains("blank")
                || exception.getResponseBodyAsString().toLowerCase().contains("must not be blank"));
    }

    @Test
    @Order(7)
    void createMembershipPlan_shouldReturnBadRequestWhenMembershipPlanRequestHasMissingName() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("type", "PREMIUM");
        request.put("monthlyPriceAmount", new BigDecimal("999.99"));
        request.put("monthlyPriceCurrencyCode", "PLN");
        request.put("durationInMonths", 6);
        request.put("maxMembers", 100);

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms/{gymId}/membership-plans", createdGymId)
                        .body(request)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().contains("name"));
    }

    @Test
    @Order(8)
    void createMembershipPlan_shouldReturnBadRequestWhenMembershipPlanDurationExceedsMax() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                61,
                100
        );

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms/{gymId}/membership-plans", createdGymId)
                        .body(request)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("duration"));
    }

    @Test
    @Order(9)
    void createMembershipPlan_shouldReturnBadRequestWhenGymIdInPathIsNegativeWhileCreatingMembershipPlan() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms/{gymId}/membership-plans", -1L)
                        .body(request)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("greater than 0"));
    }

    @Test
    @Order(10)
    void createMembershipPlan_shouldReturnNotFoundWhenCreatingMembershipPlanForNonExistingGym() {
        MembershipPlanCreateRequestDto request = MembershipPlanTestDataFactory.createRequestDto();

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms/{gymId}/membership-plans", 99L)
                        .body(request)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("gym with id"));
    }

    @Test
    @Order(11)
    void getAllMembershipPlansForGym_shouldReturnBadRequestWhenGymIdInPathIsNegativeWhileGetAllMembershipPlan() {
        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.get()
                        .uri("/gyms/{gymId}/membership-plans", -1L)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("greater than 0"));
    }

    @Test
    @Order(12)
    void getAllMembershipPlansForGym_shouldReturnBadRequestForNonExistingGym() {
        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.get()
                        .uri("/gyms/{gymId}/membership-plans", 99L)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("gym with id"));
    }
}