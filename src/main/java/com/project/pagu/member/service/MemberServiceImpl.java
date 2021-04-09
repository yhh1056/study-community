package com.project.pagu.member.service;

import com.project.pagu.member.domain.Member;
import com.project.pagu.member.domain.MemberId;
import com.project.pagu.member.domain.MemberType;
import com.project.pagu.member.domain.Role;
import com.project.pagu.member.model.MemberSaveRequestDto;
import com.project.pagu.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA
 * User: hojun
 * Date: 2021-04-02 Time: 오후 10:14
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean existsByMemberId(MemberId memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public MemberId saveMember(MemberSaveRequestDto memberSaveRequestDto) {
        Member member = Member.builder()
                .email(memberSaveRequestDto.getEmail())
                .memberType(MemberType.NORMAL)
                .nickname(memberSaveRequestDto.getNickname())
                .password(passwordEncoder.encode(memberSaveRequestDto.getPassword()))
                .role(Role.GUEST)
                .build();
        Member saveMember = memberRepository.save(member);
        return new MemberId(saveMember.getEmail(), saveMember.getMemberType());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(new MemberId(username, MemberType.NORMAL))
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
    }
}
