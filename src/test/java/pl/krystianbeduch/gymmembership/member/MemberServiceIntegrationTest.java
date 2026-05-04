package pl.krystianbeduch.gymmembership.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.exception.MemberEmailAlreadyExistsException;
import pl.krystianbeduch.gymmembership.member.exception.MemberNotFoundException;
import pl.krystianbeduch.gymmembership.member.repository.MemberRepository;
import pl.krystianbeduch.gymmembership.member.service.MemberService;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.membership.exception.MembershipPlanCapacityExceededException;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;
import pl.krystianbeduch.gymmembership.testdata.MemberTestDataFactory;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private GymRepository gymRepository;

    private Gym gym;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        membershipPlanRepository.deleteAll();
        gymRepository.deleteAll();

        gym = gymRepository.save(
                GymTestDataFactory.createGym("Gym-" + UUID.randomUUID())
        );
    }

    @Test
    void registerMemberToMembershipPlan_shouldPersistMemberWhenDataIsValid() {
        MembershipPlan membershipPlan = saveMembershipPlan(100);

        MemberRegisterToMembershipRequestDto requestDto =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        MemberResponseDto result = memberService.registerMemberToMembershipPlan(
                membershipPlan.getId(),
                requestDto
        );

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(requestDto.firstName(), result.firstName());
        assertEquals(requestDto.lastName(), result.lastName());
        assertEquals(requestDto.email(), result.email());
        assertEquals(MemberStatus.ACTIVE, result.memberStatus());

        assertEquals(1, memberRepository.count());

        Member savedMember = memberRepository.findAll().getFirst();
        assertEquals(requestDto.firstName(), savedMember.getFirstName());
        assertEquals(requestDto.lastName(), savedMember.getLastName());
        assertEquals(requestDto.email(), savedMember.getEmail());
        assertEquals(MemberStatus.ACTIVE, savedMember.getMemberStatus());
        assertEquals(membershipPlan.getId(), savedMember.getMembershipPlan().getId());
    }

    @Test
    void registerMemberToMembershipPlan_shouldThrowExceptionWhenEmailAlreadyExists() {
        MembershipPlan membershipPlan = saveMembershipPlan(100);
        String existingEmail = "john.doe@example.com";
        saveMember(existingEmail, membershipPlan);

        MemberRegisterToMembershipRequestDto requestDto =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        MemberEmailAlreadyExistsException exception = assertThrows(
                MemberEmailAlreadyExistsException.class,
                () -> memberService.registerMemberToMembershipPlan(
                        membershipPlan.getId(),
                        requestDto
                )
        );

        assertEquals(
                "Member with email '" + existingEmail + "' already exists",
                exception.getMessage()
        );
        assertEquals(1, memberRepository.count());
    }

    @Test
    void registerMemberToMembershipPlan_shouldThrowExceptionWhenMembershipPlanCapacityIsReached() {
        MembershipPlan membershipPlan = saveMembershipPlan(1);
        saveMember(membershipPlan);

        MemberRegisterToMembershipRequestDto requestDto =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        MembershipPlanCapacityExceededException exception = assertThrows(
                MembershipPlanCapacityExceededException.class,
                () -> memberService.registerMemberToMembershipPlan(
                        membershipPlan.getId(),
                        requestDto
                )
        );

        assertEquals(
                "Membership plan with id " + membershipPlan.getId() + " has reached maximum active members capacity",
                exception.getMessage()
        );
        assertEquals(1, memberRepository.count());
    }

    @Test
    void getAllMembers_shouldReturnAllPersistedMembers() {
        MembershipPlan membershipPlan = saveMembershipPlan(100);
        saveMember("jan.kowalski@onet.pl", membershipPlan);
        saveMember("jan.kowalski@wp.pl", membershipPlan);

        List<MemberResponseDto> result = memberService.getAllMembers();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(
                member -> member.email().equals("jan.kowalski@onet.pl")
        ));
        assertTrue(result.stream().anyMatch(
                member -> member.email().equals("jan.kowalski@wp.pl")
        ));
    }

    @Test
    void cancelMembership_shouldCancelMembershipWhenMemberExists() {
        MembershipPlan membershipPlan = saveMembershipPlan(100);
        Member member = saveMember(membershipPlan);

        MemberResponseDto result = memberService.cancelMembership(member.getId());

        assertNotNull(result);
        assertEquals(member.getId(), result.id());
        assertEquals(MemberStatus.CANCELLED, result.memberStatus());

        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertEquals(MemberStatus.CANCELLED, updatedMember.getMemberStatus());
    }

    @Test
    void cancelMembership_shouldThrowExceptionWhenMemberDoesNotExist() {
        Long nonExistingMemberId = 999L;

        MemberNotFoundException exception = assertThrows(
                MemberNotFoundException.class,
                () -> memberService.cancelMembership(nonExistingMemberId)
        );

        assertEquals(
                "Member with id " + nonExistingMemberId + " not found",
                exception.getMessage()
        );
    }

    private Member saveMember(MembershipPlan plan) {
        String randomEmail = UUID.randomUUID() + "@example.com";

        return memberRepository.save(
                MemberTestDataFactory.createMember(
                        randomEmail, plan
                )
        );
    }

    private void saveMember(String email, MembershipPlan plan) {
        memberRepository.save(
                MemberTestDataFactory.createMember(
                        email, plan
                )
        );
    }

    private MembershipPlan saveMembershipPlan(int maxMembers) {
        return membershipPlanRepository.save(
                MembershipPlanTestDataFactory.createMembershipPlan(
                        maxMembers, gym
                )
        );
    }
}