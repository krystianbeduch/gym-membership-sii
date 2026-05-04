package pl.krystianbeduch.gymmembership.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.exception.MemberEmailAlreadyExistsException;
import pl.krystianbeduch.gymmembership.member.exception.MemberNotFoundException;
import pl.krystianbeduch.gymmembership.member.mapper.MemberMapper;
import pl.krystianbeduch.gymmembership.member.repository.MemberRepository;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.membership.exception.MembershipPlanCapacityExceededException;
import pl.krystianbeduch.gymmembership.membership.service.MembershipPlanService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    private final MembershipPlanService membershipPlanService;

    @Transactional
    public MemberResponseDto registerMemberToMembershipPlan(
            Long membershipPlanId,
            MemberRegisterToMembershipRequestDto requestDto
    ) {
        log.info(
                "Registering member to membership plan. membershipPlanId={}, email={}",
                membershipPlanId, requestDto.email()
        );

        if (memberRepository.existsByEmail(requestDto.email())) {
            log.warn(
                    "Register member failed. Member with email={} already exists",
                    requestDto.email()
            );
            throw new MemberEmailAlreadyExistsException(
                    "Member with email '" + requestDto.email() + "' already exists"
            );
        }

        MembershipPlan membershipPlan = membershipPlanService.getMembershipPlanById(
                membershipPlanId
        );
        long activeMemberCount = memberRepository.countByMembershipPlanIdAndMemberStatus(
                membershipPlanId,
                MemberStatus.ACTIVE
        );

        if (activeMemberCount >= membershipPlan.getMaxMembers()) {
            log.warn(
                    "Member registration failed. Membership plan with id={} reached maximum capacity. activeMemberCount={}, maxMembers={}",
                    membershipPlanId,
                    activeMemberCount,
                    membershipPlan.getMaxMembers()
            );
            throw new MembershipPlanCapacityExceededException(
                    "Membership plan with id " + membershipPlanId + " has reached maximum active members capacity"
            );
        }
        Member member = memberMapper.requestDtoToEntity(requestDto, membershipPlan);
        Member savedMember = memberRepository.save(member);

         log.info(
                "Member registered successfully. id={}, membershipPlanId={}, email={}",
                savedMember.getId(),
                membershipPlanId,
                savedMember.getEmail()
        );

        return memberMapper.entityToResponseDto(savedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> getAllMembers() {
        log.info("Fetching all members");

        List<MemberResponseDto> members = memberRepository.findAll().stream()
                .map(memberMapper::entityToResponseDto)
                .toList();

        log.debug("Fetched {} members", members.size());
        return members;
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembersEntityByStatus(MemberStatus memberStatus) {
        log.info("Fetching all members by status={}", memberStatus);

        return memberRepository.findAllByMemberStatus(memberStatus);
    }

    @Transactional
    public MemberResponseDto cancelMembership(Long memberId) {
        log.info("Cancelling membership for memberId={}", memberId);
        Member member = getMemberById(memberId);
        member.cancelMembership();
        return memberMapper.entityToResponseDto(member);
    }



    private Member getMemberById(Long id) {
        log.info("Fetching member with id={}", id);

        return memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("Member with id " + id + " not found")
        );
    }
}