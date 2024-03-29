package com.project.pagu.modules.board.service;

import static java.time.Month.APRIL;
import static java.time.Month.MAY;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.pagu.common.manager.FileManager;
import com.project.pagu.modules.board.domain.Board;
import com.project.pagu.modules.board.domain.StudyStatus;
import com.project.pagu.modules.board.model.BoardViewDto;
import com.project.pagu.modules.board.model.BoardSaveDto;
import com.project.pagu.modules.board.model.BoardScheduleDto;
import com.project.pagu.modules.board.repository.BoardRepository;
import com.project.pagu.modules.comment.service.CommentSaveService;
import com.project.pagu.modules.comment.service.CommentViewService;
import com.project.pagu.modules.member.domain.Member;
import com.project.pagu.modules.member.domain.MemberType;
import com.project.pagu.modules.member.domain.Role;
import com.project.pagu.modules.member.service.MemberSaveService;
import com.project.pagu.modules.member.service.MemberViewService;
import com.project.pagu.modules.tag.Subject;
import com.project.pagu.modules.tag.SubjectService;
import com.project.pagu.modules.teckstack.TechStack;
import com.project.pagu.modules.teckstack.TechStackService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by IntelliJ IDEA
 * User: yhh1056@naver.com
 * Date: 2021/04/27 Time: 4:15 오후
 */

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardSaveServiceImpl boardSaveService;

    @InjectMocks
    private BoardViewServiceImpl boardViewService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private FileManager fileManager;

    @Mock
    private MemberViewService memberViewService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private TechStackService techStackService;

    @Mock
    private CommentSaveService commentSaveService;

    @Mock
    private CommentViewService commentViewService;

    @Captor
    private ArgumentCaptor<com.project.pagu.modules.board.domain.Board> argumentCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Test
    @DisplayName("게시물을 등록한다.")
    void save_board() {
        Member member = givenMember();
        given(memberViewService.findById(any())).willReturn(member);
        given(subjectService.getOrSave(any())).willReturn(Subject.of("Spring"));
        given(techStackService.getOrSave(any())).willReturn(TechStack.of("Spring"));
        given(boardRepository.save(any())).willReturn(givenBoard(1L));
        given(fileManager.uploadBoardImageDtos(any(),any())).willReturn(null);

        boardSaveService.saveBoardDto(member.getMemberId(), givenBoardDto());

        verify(boardRepository, times(1)).save(argumentCaptor.capture());

        then(argumentCaptor.getValue()).isNotNull();
        then(argumentCaptor.getValue().getTitle()).isEqualTo("스프링 스터디 모집합니다.");
        then(argumentCaptor.getValue().getBoardSubjects().size()).isEqualTo(1);
        then(argumentCaptor.getValue().getBoardTechStacks().size()).isEqualTo(1);
        then(argumentCaptor.getValue().getGoal()).isEqualTo("스프링링의 기본 원리를 이해한다.");
        then(argumentCaptor.getValue().getBoardSchedules().size()).isEqualTo(1);
        then(argumentCaptor.getValue().getRecruitmentStartAt().getMonth()).isEqualTo(APRIL);
        then(argumentCaptor.getValue().getRecruitmentEndAt().getMonth()).isEqualTo(MAY);
        then(argumentCaptor.getValue().getTermsStartAt().getMonth()).isEqualTo(MAY);
        then(argumentCaptor.getValue().getTermsEndAt().getMonth()).isEqualTo(MAY);
        then(argumentCaptor.getValue().getEtc()).isEqualTo("기타 사항");
    }

    @Test
    @DisplayName("페이징 처리 된 게시물들을 가져온다.")
    void get_paged_board_list() {

        // given
        Pageable pageable = givenPageable(0, 10, Sort.by("modifiedDate").descending());
        given(boardRepository.findAll(pageable)).willReturn(givenPagedBoard(pageable));

        // when
        boardViewService.getPagedBoardList(pageable);

        // then
        verify(boardRepository, times(1)).findAll(pageableArgumentCaptor.capture());
        then(pageableArgumentCaptor.getValue()).isNotNull();
        then(pageableArgumentCaptor.getValue().getPageSize()).isEqualTo(10);
        then(pageableArgumentCaptor.getValue().getPageNumber()).isEqualTo(0);
        then(pageableArgumentCaptor.getValue().getSort())
                .isEqualTo(Sort.by("modifiedDate").descending());

    }

    @Test
    @DisplayName("게시물 상세 조회 한다.")
    void get_board_detail() throws Exception {
        // given
        Board board = givenBoard(1L);
        given(boardRepository.findById(any())).willReturn(Optional.of(board));
        given(commentViewService.findCommentsByBoardId(any())).willReturn(null);
        // when
        BoardViewDto boardViewDto = boardViewService.getBoardDetailDto(1L);

        // then
        verify(boardRepository, times(1)).findById(1L);

        then(boardViewDto.getId()).isEqualTo(board.getId());
        then(boardViewDto.getTitle()).isEqualTo(board.getTitle());
        then(boardViewDto.getGoal()).isEqualTo(board.getGoal());
        then(boardViewDto.getPlace()).isEqualTo(board.getPlace());
        then(boardViewDto.getWriter().getNickname()).isEqualTo(board.getMember().getNickname());
        then(boardViewDto.getWriter().getEmail()).isEqualTo(board.getMember().getEmail());
        then(boardViewDto.getRecruitmentStartAt()).isEqualTo(board.getRecruitmentStartAt());
        then(boardViewDto.getRecruitmentEndAt()).isEqualTo(board.getRecruitmentEndAt());
        then(boardViewDto.getTermsEndAt()).isEqualTo(board.getTermsEndAt());
        then(boardViewDto.getTermsStartAt()).isEqualTo(board.getTermsStartAt());
        then(boardViewDto.getStatus()).isEqualTo(board.getStatus());
        then(boardViewDto.getModifiedDate()).isEqualTo(board.getModifiedDate());
        then(boardViewDto.getEtc()).isEqualTo(board.getEtc());
    }


    private Member givenMember() {
        return Member.builder()
                .email("test@email.com")
                .memberType(MemberType.NORMAL)
                .nickname("tester")
                .role(Role.GUEST)
                .imageFilename(null)
                .career("취준생")
                .postion("백엔드")
                .link("test@gi.com")
                .info("안녕하세요")
                .build();
    }

    private BoardSaveDto givenBoardDto() {
        BoardScheduleDto scheduleDto = new BoardScheduleDto();
        scheduleDto.setDayKey(0);
        scheduleDto.setStartTime(LocalTime.of(10, 30));
        scheduleDto.setEndTime(LocalTime.of(13, 30));

        BoardSaveDto dto = new BoardSaveDto();
        dto.setTitle("스프링 스터디 모집합니다.");
        dto.setSubjects("Backend");
        dto.setTechStacks("Java");
        dto.setGoal("스프링링의 기본 원리를 이해한다.");
        dto.setBoardSchedules(List.of(scheduleDto));
        dto.setRecruitmentStartAt(LocalDate.of(2021, 04, 27));
        dto.setRecruitmentEndAt(LocalDate.of(2021, 05, 4));
        dto.setTermsStartAt(LocalDate.of(2021, 05, 12));
        dto.setTermsEndAt(LocalDate.of(2021, 05, 24));
        dto.setEtc("기타 사항");
        return dto;
    }

    private Pageable givenPageable(int page, int size, Sort sort) {
        return PageRequest.of(page, size, sort);
    }

    private PageImpl<Board> givenPagedBoard(Pageable pageable) {
        List<com.project.pagu.modules.board.domain.Board> boards = givenBoardList();
        return new PageImpl<>(boards, pageable, boards.size());
    }

    private List<Board> givenBoardList() {
        List<com.project.pagu.modules.board.domain.Board> boards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            boards.add(givenBoard((long) i));
        }
        return boards;
    }

    private Board givenBoard(Long id) {
        return com.project.pagu.modules.board.domain.Board.builder()
                .id(id)
                .title("제목")
                .goal("목표")
                .place("강남역 ")
                .member(givenMember())
                .recruitmentStartAt(LocalDate.now())
                .recruitmentEndAt(LocalDate.now().plusDays(30))
                .termsStartAt(LocalDate.now().plusDays(30))
                .termsEndAt(LocalDate.now().plusDays(30))
                .status(StudyStatus.READY)
                .etc("기타 사항").build();

    }

}