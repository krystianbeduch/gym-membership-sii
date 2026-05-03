package pl.krystianbeduch.gymmembership.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberRegisterToMembershipRequestDto(
        @NotBlank(message = "{member.firstName.notBlank}")
        @Size(max = 64, message = "{member.firstName.size}")
        String firstName,

        @NotBlank(message = "{member.lastName.notBlank}")
        @Size(max = 64, message = "{member.lastName.size}")
        String lastName,

        @NotBlank(message = "{member.email.notBlank}")
        @Size(max = 100, message = "{member.email.size}")
        @Email(message = "{member.email.email}")
        String email
) { }