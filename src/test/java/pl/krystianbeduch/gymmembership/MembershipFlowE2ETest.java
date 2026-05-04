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
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.repository.MemberRepository;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;
import pl.krystianbeduch.gymmembership.testdata.MemberTestDataFactory;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("e2e-test")
class MembershipFlowE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private MemberRepository memberRepository;

    private RestClient restClient;

    private Long createdGymId;
    private String createdGymName;
    private Long createdMembershipPlanId;

    private Long createdMemberId;
    private String createdMemberEmail;

    @BeforeAll
    void cleanDb() {
        memberRepository.deleteAll();
        membershipPlanRepository.deleteAll();
        gymRepository.deleteAll();
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

    /// =========================================================
    /// Happy path workflow - GymController
    /// =========================================================
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

        GymResponseDto body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals(uniqueName, body.name());

        createdGymId = body.id();
        createdGymName = body.name();
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

    /// =========================================================
    /// Happy path workflow - MembershipPlanController
    /// =========================================================
    @Test
    @Order(3)
    void createMembershipPlan_shouldCreateMembershipPlanForCreatedGym() {
        MembershipPlanCreateRequestDto request =
                MembershipPlanTestDataFactory.createRequestDto();

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

        List<MembershipPlanResponseDto> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertTrue(body.stream()
                .anyMatch(plan -> plan.id().equals(createdMembershipPlanId)
                        && plan.gymId().equals(createdGymId)
                        && plan.name().equals("Premium 6M"))
        );
    }

    /// =========================================================
    /// Happy path workflow - MemberController
    /// =========================================================
    @Test
    @Order(5)
    void registerMemberToMembershipPlan_shouldCreateMemberForCreatedMembershipPlan() {
        MemberRegisterToMembershipRequestDto request =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "Doe",
                        "john.happy@example.com"
                );

        ResponseEntity<MemberResponseDto> response = restClient.post()
                .uri("/membership-plans/{membershipPlanId}/members", createdMembershipPlanId)
                .body(request)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        MemberResponseDto body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.id());
        assertEquals("John", body.firstName());
        assertEquals("Doe", body.lastName());
        assertEquals("john.happy@example.com", body.email());
        assertEquals(MemberStatus.ACTIVE, body.memberStatus());
        assertEquals(createdMembershipPlanId, body.membershipPlanId());
        assertEquals(createdGymId, body.gymId());

        createdMemberId = body.id();
        createdMemberEmail = body.email();
    }

    @Test
    @Order(6)
    void getAllMembers_shouldReturnAllMembersAndContainCreatedMember() {
        ResponseEntity<List<MemberResponseDto>> response = restClient.get()
                .uri("/members")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        List<MemberResponseDto> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertTrue(body.stream()
                .anyMatch(member -> member.id().equals(createdMemberId)
                        && member.email().equals(createdMemberEmail)
                        && member.membershipPlanId().equals(createdMembershipPlanId))
        );
    }

    @Test
    @Order(7)
    void cancelMembership_shouldCancelMembershipForCreatedMember() {
        ResponseEntity<MemberResponseDto> response = restClient.post()
                .uri("/members/{memberId}/cancel-membership", createdMemberId)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        MemberResponseDto body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals(createdMemberId, body.id());
        assertEquals(createdMemberEmail, body.email());
        assertEquals(MemberStatus.CANCELLED, body.memberStatus());
    }

    @Test
    @Order(8)
    void getAllMembers_shouldReturnCancelledMemberAfterCancellation() {
        ResponseEntity<List<MemberResponseDto>> response = restClient.get()
                .uri("/members")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        List<MemberResponseDto> members = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(members);
        assertTrue(members.stream()
                .anyMatch(member -> member.id().equals(createdMemberId)
                        && member.memberStatus() == MemberStatus.CANCELLED));
    }

    /// =========================================================
    /// Bad requests - GymController
    /// =========================================================
    @Test
    @Order(9)
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
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("blank"));
    }

    /// =========================================================
    /// Bad requests - MembershipPlanController
    /// =========================================================
    @Test
    @Order(10)
    void createMembershipPlan_shouldReturnBadRequestWhenMembershipPlanRequestHasBlankName() {
        MembershipPlanCreateRequestDto request =
                MembershipPlanTestDataFactory.createRequestDto("  ");

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/gyms/{gymId}/membership-plans", createdGymId)
                        .body(request)
                        .retrieve()
                        .toEntity(MembershipPlanResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("blank"));
    }

    @Test
    @Order(11)
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
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("name"));
    }

    @Test
    @Order(12)
    void createMembershipPlan_shouldReturnBadRequestWhenMembershipPlanDurationExceedsMax() {
        MembershipPlanCreateRequestDto request =
                MembershipPlanTestDataFactory.createRequestDto(61);

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
    @Order(13)
    void createMembershipPlan_shouldReturnBadRequestWhenGymIdInPathIsNegativeWhileCreatingMembershipPlan() {
        MembershipPlanCreateRequestDto request =
                MembershipPlanTestDataFactory.createRequestDto();

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
    @Order(14)
    void createMembershipPlan_shouldReturnNotFoundWhenCreatingMembershipPlanForNonExistingGym() {
        MembershipPlanCreateRequestDto request =
                MembershipPlanTestDataFactory.createRequestDto();

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
    @Order(15)
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
    @Order(16)
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

    /// =========================================================
    /// Bad requests - MemberController
    /// =========================================================
    @Test
    @Order(17)
    void registerMemberToMembershipPlan_shouldReturnBadRequestWhenRequestHasBlankFirstName() {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto("  ");

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/membership-plans/{membershipPlanId}/members", createdMembershipPlanId)
                        .body(request)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("first name"));
    }

    @Test
    @Order(18)
    void registerMemberToMembershipPlan_shouldReturnBadRequestWhenRequestHasInvalidEmail() {
        MemberRegisterToMembershipRequestDto request =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "Doe",
                        "invalid-email"
                );

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/membership-plans/{membershipPlanId}/members", createdMembershipPlanId)
                        .body(request)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("email"));
    }

    @Test
    @Order(19)
    void registerMemberToMembershipPlan_shouldReturnBadRequestWhenMembershipPlanIdIsNegative() {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/membership-plans/{membershipPlanId}/members", -1L)
                        .body(request)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("greater than 0"));
    }

    @Test
    @Order(20)
    void registerMemberToMembershipPlan_shouldReturnNotFoundWhenMembershipPlanDoesNotExist() {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/membership-plans/{membershipPlanId}/members", 99L)
                        .body(request)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
    }

    @Test
    @Order(21)
    void registerMemberToMembershipPlan_shouldReturnConflictWhenEmailAlreadyExists() {
        String duplicatedEmail = "duplicate-" + UUID.randomUUID() + "@example.com";

        MemberRegisterToMembershipRequestDto firstRequest =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "Doe",
                        duplicatedEmail
                );

        ResponseEntity<MemberResponseDto> firstResponse = restClient.post()
                .uri("/membership-plans/{membershipPlanId}/members", createdMembershipPlanId)
                .body(firstRequest)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        MemberRegisterToMembershipRequestDto secondRequest =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "Doe",
                        duplicatedEmail
                );

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/membership-plans/{membershipPlanId}/members", createdMembershipPlanId)
                        .body(secondRequest)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("already exists")
                && exception.getResponseBodyAsString().toLowerCase().contains("email"));
    }

    @Test
    @Order(22)
    void registerMemberToMembershipPlan_shouldReturnConflictWhenMembershipPlanCapacityIsReached() {


        MembershipPlanCreateRequestDto request = MembershipPlanCreateRequestDto.builder()
                .name("Limited Plan")
                .type(MembershipPlanType.BASIC)
                .monthlyPriceAmount(new BigDecimal("49.99"))
                .monthlyPriceCurrencyCode("PLN")
                .durationInMonths(1)
                .maxMembers(2)
                .build();

        ResponseEntity<MembershipPlanResponseDto> createdPlanResponse = restClient.post()
                .uri("/gyms/{gymId}/membership-plans", createdGymId)
                .body(request)
                .retrieve()
                .toEntity(MembershipPlanResponseDto.class);

        assertEquals(HttpStatus.CREATED, createdPlanResponse.getStatusCode());
        assertNotNull(createdPlanResponse.getBody());
        Long limitedPlanId = createdPlanResponse.getBody().id();

        MemberRegisterToMembershipRequestDto member1 =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "One",
                        "john.one@example.com"
                );

        MemberRegisterToMembershipRequestDto member2 =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "Two",
                        "john.two@example.com"
                );

        MemberRegisterToMembershipRequestDto member3 =
                new MemberRegisterToMembershipRequestDto(
                        "John",
                        "Three",
                        "john.three@example.com"
                );

        ResponseEntity<MemberResponseDto> response1 = restClient.post()
                .uri("/membership-plans/{membershipPlanId}/members", limitedPlanId)
                .body(member1)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        ResponseEntity<MemberResponseDto> response2 = restClient.post()
                .uri("/membership-plans/{membershipPlanId}/members", limitedPlanId)
                .body(member2)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/membership-plans/{membershipPlanId}/members", limitedPlanId)
                        .body(member3)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.CONFLICT.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("maximum active members"));
    }

    @Test
    @Order(23)
    void cancelMembership_shouldReturnBadRequestWhenMemberIdIsNegative() {
        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/members/{memberId}/cancel-membership", -1L)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("greater than 0"));
    }

    @Test
    @Order(24)
    void cancelMembership_shouldReturnNotFoundWhenMemberDoesNotExist() {
        RestClientResponseException exception = assertThrows(
                RestClientResponseException.class,
                () -> restClient.post()
                        .uri("/members/{memberId}/cancel-membership", 99L)
                        .retrieve()
                        .toEntity(MemberResponseDto.class)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        assertTrue(exception.getResponseBodyAsString().toLowerCase().contains("member with id"));
    }
}