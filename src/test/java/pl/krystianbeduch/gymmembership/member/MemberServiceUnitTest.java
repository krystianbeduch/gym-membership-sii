package pl.krystianbeduch.gymmembership.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.exception.MemberEmailAlreadyExistsException;
import pl.krystianbeduch.gymmembership.member.exception.MemberNotFoundException;
import pl.krystianbeduch.gymmembership.member.mapper.MemberMapper;
import pl.krystianbeduch.gymmembership.member.repository.MemberRepository;
import pl.krystianbeduch.gymmembership.member.service.MemberService;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.membership.exception.MembershipPlanCapacityExceededException;
import pl.krystianbeduch.gymmembership.membership.service.MembershipPlanService;
import pl.krystianbeduch.gymmembership.testdata.MemberTestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceUnitTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private MembershipPlanService membershipPlanService;

    @InjectMocks
    private MemberService memberService;

    @Test
    void registerMemberToMembershipPlan_shouldRegisterMemberSuccessfully() {
        Long membershipPlanId = 1L;
        MemberRegisterToMembershipRequestDto requestDto = MemberTestDataFactory.createMemberRegisterRequestDto();

        MembershipPlan membershipPlan = MembershipPlan.builder()
                .id(membershipPlanId)
                .maxMembers(100)
                .build();

        Member member = Member.builder()
                .email(requestDto.email())
                .membershipPlan(membershipPlan)
                .build();

        Member savedMember = Member.builder()
                .id(100L)
                .email(requestDto.email())
                .membershipPlan(membershipPlan)
                .build();

        MemberResponseDto responseDto = MemberTestDataFactory.createMemberResponseDto(
                100L, "john.doe@example.com"
        );

        when(memberRepository.existsByEmail(requestDto.email()))
                .thenReturn(false);
        when(membershipPlanService.getMembershipPlanById(membershipPlanId))
                .thenReturn(membershipPlan);
        when(memberRepository.countByMembershipPlanIdAndMemberStatus(
                membershipPlanId, MemberStatus.ACTIVE
        )).thenReturn(10L);
        when(memberMapper.requestDtoToEntity(requestDto, membershipPlan))
                .thenReturn(member);
        when(memberRepository.save(member))
                .thenReturn(savedMember);
        when(memberMapper.entityToResponseDto(savedMember))
                .thenReturn(responseDto);

        MemberResponseDto result = memberService.registerMemberToMembershipPlan(
                membershipPlanId, requestDto
        );

        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(requestDto.email(), result.email());
        assertEquals(MemberStatus.ACTIVE, result.memberStatus());

        verify(memberRepository).existsByEmail(requestDto.email());
        verify(membershipPlanService).getMembershipPlanById(membershipPlanId);
        verify(memberRepository).countByMembershipPlanIdAndMemberStatus(
                membershipPlanId, MemberStatus.ACTIVE
        );
        verify(memberMapper).requestDtoToEntity(requestDto, membershipPlan);
        verify(memberRepository).save(member);
        verify(memberMapper).entityToResponseDto(savedMember);
        verifyNoMoreInteractions(memberRepository, memberMapper, membershipPlanService);
    }

    @Test
    void registerMemberToMembershipPlan_shouldThrowMemberEmailAlreadyExistsExceptionWhenEmailExists() {
        Long membershipPlanId = 1L;
        MemberRegisterToMembershipRequestDto requestDto = MemberTestDataFactory.createMemberRegisterRequestDto();

        when(memberRepository.existsByEmail(requestDto.email()))
                .thenReturn(true);

        MemberEmailAlreadyExistsException exception = assertThrows(
                MemberEmailAlreadyExistsException.class,
                () -> memberService.registerMemberToMembershipPlan(
                        membershipPlanId, requestDto
                )
        );

        assertEquals(
                "Member with email '" + requestDto.email() + "' already exists",
                exception.getMessage()
        );
        verify(memberRepository).existsByEmail(requestDto.email());
        verifyNoInteractions(membershipPlanService);
        verify(memberRepository, never()).countByMembershipPlanIdAndMemberStatus(anyLong(), any());
        verify(memberRepository, never()).save(any());
        verifyNoInteractions(memberMapper);
    }

    @Test
    void registerMemberToMembershipPlan_shouldThrowMembershipPlanCapacityExceededExceptionWhenCapacityIsReached() {
        Long membershipPlanId = 1L;
        MemberRegisterToMembershipRequestDto requestDto = MemberTestDataFactory.createMemberRegisterRequestDto();

        MembershipPlan membershipPlan = MembershipPlan.builder()
                .id(membershipPlanId)
                .maxMembers(150)
                .build();

        when(memberRepository.existsByEmail(requestDto.email()))
                .thenReturn(false);
        when(membershipPlanService.getMembershipPlanById(membershipPlanId))
                .thenReturn(membershipPlan);
        when(memberRepository.countByMembershipPlanIdAndMemberStatus(
                membershipPlanId, MemberStatus.ACTIVE
        )).thenReturn(150L);

        MembershipPlanCapacityExceededException exception = assertThrows(
                MembershipPlanCapacityExceededException.class,
                () -> memberService.registerMemberToMembershipPlan(
                        membershipPlanId, requestDto
                )
        );

        assertEquals(
                "Membership plan with id " + membershipPlanId + " has reached maximum active members capacity",
                exception.getMessage()
        );

        verify(memberRepository).existsByEmail(requestDto.email());
        verify(membershipPlanService).getMembershipPlanById(membershipPlanId);
        verify(memberRepository).countByMembershipPlanIdAndMemberStatus(
                membershipPlanId, MemberStatus.ACTIVE
        );
        verify(memberRepository, never()).save(any());
        verify(memberMapper, never()).requestDtoToEntity(any(), any());
        verify(memberMapper, never()).entityToResponseDto(any());
    }

    @Test
    void getAllMembers_shouldReturnAllMembers() {
        Member member1 = Member.builder()
                .id(1L)
                .email("john.doe@example.com")
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .email("jan.kowalski@gmail.com")
                .build();

        MemberResponseDto response1 = MemberTestDataFactory.createMemberResponseDto(
                1L, "john.doe@example.com"
        );
        MemberResponseDto response2 = MemberTestDataFactory.createMemberResponseDto(
                2L, "jan.kowalski@gmail.com"
        );

        when(memberRepository.findAll())
                .thenReturn(List.of(member1, member2));
        when(memberMapper.entityToResponseDto(member1))
                .thenReturn(response1);
        when(memberMapper.entityToResponseDto(member2))
                .thenReturn(response2);

        List<MemberResponseDto> result = memberService.getAllMembers();

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("john.doe@example.com", result.get(0).email());
        assertEquals(2L, result.get(1).id());
        assertEquals("jan.kowalski@gmail.com", result.get(1).email());

        verify(memberRepository).findAll();
        verify(memberMapper).entityToResponseDto(member1);
        verify(memberMapper).entityToResponseDto(member2);
        verifyNoMoreInteractions(memberRepository, memberMapper);
    }

    @Test
    void cancelMembership_shouldCancelMembershipSuccessfully() {
        Long memberId = 1L;

        Member member = Member.builder()
                .id(memberId)
                .memberStatus(MemberStatus.ACTIVE)
                .membershipPlan(new MembershipPlan())
                .build();

        MemberResponseDto responseDto = MemberTestDataFactory.createMemberResponseDto();

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));
        when(memberMapper.entityToResponseDto(member))
                .thenReturn(responseDto);

        MemberResponseDto result = memberService.cancelMembership(memberId);

        assertEquals(responseDto, result);
        assertEquals(MemberStatus.CANCELLED, member.getMemberStatus());

        verify(memberRepository).findById(memberId);
        verify(memberMapper).entityToResponseDto(member);
    }

    @Test
    void cancelMembership_shouldThrowMemberNotFoundExceptionWhenMemberDoesNotExist() {
        Long memberId = 1L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(
                MemberNotFoundException.class,
                () -> memberService.cancelMembership(memberId)
        );

        assertEquals(
                "Member with id " + memberId + " not found",
                exception.getMessage()
        );

        verify(memberRepository).findById(memberId);
        verify(memberMapper, never()).entityToResponseDto(any());
    }
}