package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.MemberNotActivatedException;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentServiceImpl implements RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberService memberService;
    @Override
    public RecruitmentResponseDto findById(Long recruitmentId) {
        Recruitment byId = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        return new RecruitmentResponseDto(byId);
    }

    @Override
    public List<RecruitmentResponseDto> findAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto) {
        String currentUsername = memberService.getCurrentUsername();
        MemberResponseDto currentMember = memberService.getMemberWithAuthorities(currentUsername);

        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember.convertToEntity());
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }

    @Override
    public void delete(Long recruitmentId) {
        recruitmentRepository.deleteById(recruitmentId);
    }

    @Override
    public RecruitmentResponseDto update(Long recruitmentId,
                                         RecruitmentRequestDto update) {
        Recruitment recruitment = Recruitment.builder()
                .id(recruitmentId)
                .title(update.getTitle())
                .content(update.getContent())
                .build();

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }

    @Override
    public List<RecruitmentResponseDto> findAllByRestaurantName(String restaurantName) {
        return recruitmentRepository.findAllByRestaurantName(restaurantName).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public RecruitmentResponseDto joinOrUnJoin(Long recruitmentId) {
        String currentUsername = memberService.getCurrentUsername();
        MemberResponseDto memberDto = memberService.getMemberWithAuthorities(currentUsername);
        Member currentMember = memberDto.convertToEntity();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow(() -> {
            throw new RecruitmentNotFoundException(recruitmentId);
        });
        validateRecruitment(recruitment);

        if (isAlreadyJoin(recruitment, currentMember)) {
            return new RecruitmentResponseDto(unJoin(recruitment, currentMember));
        } else {
            return new RecruitmentResponseDto(join(recruitment, currentMember));
        }
    }

    private Recruitment join(Recruitment recruitment, Member member) throws RecruitmentIsFullException, RecruitmentNotActiveException {
        validateRecruitment(recruitment);
        if (!member.isActive()) {
            throw new MemberNotActivatedException(member.getUsername());
        }
        recruitment.getMembers().add(member);
        increaseCurrentNumberOfPeople(recruitment);

        return recruitmentRepository.save(recruitment);
    }

    public Recruitment unJoin(Recruitment recruitment, Member member) {
        recruitment.getMembers().remove(member);
        decreaseCurrentNumberOfPeople(recruitment);
        return recruitmentRepository.save(recruitment);
    }

    private void increaseCurrentNumberOfPeople(Recruitment recruitment) {
        validateRecruitment(recruitment);
        int currentNumberOfPeople = recruitment.getCurrentNumberOfPeople() + 1;
        recruitment.setCurrentNumberOfPeople(currentNumberOfPeople);
        if (currentNumberOfPeople >= recruitment.getTotalNumberOfPeople()) {
            recruitment.setFull(true);
        }
    }

    private void decreaseCurrentNumberOfPeople(Recruitment recruitment) {
        recruitment.setCurrentNumberOfPeople(recruitment.getCurrentNumberOfPeople() - 1);
    }

    private void validateRecruitment(Recruitment recruitment) {
        if (recruitment.isFull()) throw new RecruitmentIsFullException();
        if (!recruitment.isActive()) throw new RecruitmentNotActiveException(recruitment.getId());
    }

    private boolean isAlreadyJoin(Recruitment recruitment, Member member) {
        return recruitment.getMembers().contains(member);
    }
}
