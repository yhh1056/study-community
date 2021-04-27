package com.project.pagu.modules.board.model;

import com.project.pagu.common.manager.FileUtil;
import com.project.pagu.modules.board.domain.Board;
import com.project.pagu.modules.board.domain.BoardImage;
import com.project.pagu.modules.board.domain.BoardSchedule;
import com.project.pagu.modules.board.domain.StudyStatus;
import com.project.pagu.modules.tag.BoardSubject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA
 * User: hojun
 * Date: 2021-04-26 Time: 오후 8:12
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardDetailDto {

    private Long id;

    private String title;

    @Builder.Default
    private List<String> subjects = new ArrayList<>();

    private String techs;

    private String goal;

    private String place;

    @Builder.Default
    private List<BoardScheduleDto> schedules = new ArrayList<>();

    private LocalDate recruitmentStartAt;

    private LocalDate recruitmentEndAt;

    private LocalDate termsStartAt;

    private LocalDate termsEndAt;

    private String etc;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    private StudyStatus status;

    private LocalDateTime modifiedDate;

    private WriterDto writer;

    public static BoardDetailDto CreateBoardDetailDto(Board board) {

        BoardDetailDto boardDetailDto = BoardDetailDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .techs(board.getTechStacks())
                .goal(board.getGoal())
                .place(board.getPlace())
                .recruitmentStartAt(board.getRecruitmentStartAt())
                .recruitmentEndAt(board.getRecruitmentEndAt())
                .termsStartAt(board.getTermsStartAt())
                .termsEndAt(board.getTermsEndAt())
                .etc(board.getEtc())
                .status(board.getStatus())
                .modifiedDate(board.getModifiedDate())
                .writer(WriterDto.createWriterDto(board.getMember()))
                .build();
        boardDetailDto.addImageUrls(board.getId(), board.getBoardImages());
        boardDetailDto.addSubjects(board.getBoardSubjects());
        boardDetailDto.addSchedules(board.getBoardSchedules());
        return boardDetailDto;

    }

    private void addImageUrls(Long boardId, List<BoardImage> boardImages) {
        for (BoardImage boardImage : boardImages) {
            this.imageUrls.add(FileUtil
                    .createImageUrl("boardImageThumbnails", String.valueOf(boardId),
                            boardImage.getFilename()));
        }
    }

    private void addSchedules(List<BoardSchedule> boardSchedules) {
        for (BoardSchedule boardSchedule : boardSchedules) {
            this.schedules.add(BoardScheduleDto.createBoardScheduleDto(boardSchedule));
        }
    }

    private void addSubjects(Set<BoardSubject> boardSubjects) {
        for (BoardSubject boardSubject : boardSubjects) {
            this.subjects.add(boardSubject.getSubject().getName());
        }
    }


}
