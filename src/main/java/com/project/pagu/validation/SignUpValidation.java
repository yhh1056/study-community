package com.project.pagu.validation;

import com.project.pagu.member.model.MemberSaveRequestDto;
import com.project.pagu.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA
 * User: yhh1056@naver.com
 * Date: 2021/04/06 Time: 5:54 오후
 */

@RequiredArgsConstructor
@Configuration
public class SignUpValidation implements Validator {

    private final MemberService memberService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(MemberSaveRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberSaveRequestDto memberSaveRequestDto = (MemberSaveRequestDto) target;
        isExistedEmail(memberSaveRequestDto.getEmail(), errors);
        isExistedNickname(memberSaveRequestDto.getNickname(), errors);
    }

    private void isExistedEmail(String email, Errors errors) {
        if (memberService.existsByEmail(email)) {
            errors.rejectValue("email", "UniqueEmail", "이미 존재하는 이메일입니다.");
        }
    }

    private void isExistedNickname(String nickname, Errors errors) {
        if (memberService.existsByNickname(nickname)) {
            errors.rejectValue("nickname", "UniqueNickname", "이미 존재하는 닉네임입니다.");
        }
    }
}
