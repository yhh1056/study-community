package com.project.pagu.member.controller;

import com.project.pagu.annotation.CurrentMember;
import com.project.pagu.common.file.FileManager;
import com.project.pagu.member.domain.Member;
import com.project.pagu.member.model.ProfileImageDto;
import com.project.pagu.member.model.ProfileRequestDto;
import com.project.pagu.member.service.MemberService;
import com.project.pagu.validation.ProfileValidation;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by IntelliJ IDEA
 * User: yhh1056@naver.com
 * Date: 2021/04/16 Time: 4:02 오후
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final MemberService memberService;
    private final ProfileValidation profileValidation;
    private final FileManager fileManager;

    @InitBinder("profileRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileValidation);
    }

    @GetMapping("/profile")
    public String profile(@CurrentMember Member member, Model model) {
        ProfileRequestDto profileRequestDto = memberService.convertMemberToProfileRequestDto(member);
        model.addAttribute(profileRequestDto);
        return "profile";
    }

    @PostMapping("/members/update")
    public String updateMember(@CurrentMember Member member, @Valid ProfileRequestDto profileRequestDto, BindingResult result) {

        if (result.hasErrors()) {
            return "profile";
        }

        memberService.update(member, profileRequestDto);
        return "redirect:/profile";
    }

    @GetMapping("/profileThumbnails/{email}/{type}/{filename}")
    public void profileThumbnails(ProfileImageDto profileImageDto, HttpServletResponse response)
            throws Exception {
        fileManager.profileThumbnails(profileImageDto, response);
    }

}
