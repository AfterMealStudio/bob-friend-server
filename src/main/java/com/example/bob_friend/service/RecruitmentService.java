package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberService memberService;

    public RecruitmentDto.Response findById(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        return new RecruitmentDto.Response(recruitment);
    }

    public List<RecruitmentDto.Response> findAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    public RecruitmentDto.Response createRecruitment(RecruitmentDto.Request recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember);
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentDto.Response(savedRecruitment);
    }

    public void delete(Long recruitmentId) {
        recruitmentRepository.deleteById(recruitmentId);
    }

    public RecruitmentDto.Response update(Long recruitmentId,
                                          RecruitmentDto.Request update) {
        Recruitment recruitment = Recruitment.builder()
                .id(recruitmentId)
                .title(update.getTitle())
                .content(update.getContent())
                .build();

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentDto.Response(savedRecruitment);
    }

    public List<RecruitmentDto.Response> findAllByRestaurantNameOrRestaurantAddress(
            String restaurantName,
            String restaurantAddress) {
        return recruitmentRepository.findAllByRestaurantNameOrRestaurantAddress(restaurantName,restaurantAddress).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<RecruitmentDto.Response> findAllAvailableRecruitments() {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        !recruitment.hasMember(currentMember) &&
                                !recruitment.getAuthor().equals(currentMember)
                ).map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<RecruitmentDto.Response> findAllJoinedRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        recruitment.hasMember(author) ||
                                recruitment.getAuthor().equals(author)
                ).map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<RecruitmentDto.Response> findMyRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecruitmentDto.Response joinOrUnjoin(Long recruitmentId) throws RecruitmentIsFullException, RecruitmentNotActiveException {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });

        validateRecruitment(recruitment);

        if (recruitment.hasMember(currentMember))
            recruitment.removeMember(currentMember);
        else
            recruitment.addMember(currentMember);

        return new RecruitmentDto.Response(recruitment);
    }

    private void validateRecruitment(Recruitment recruitment) {
        if (recruitment.isFull())
            throw new RecruitmentIsFullException(recruitment.getId());
        if (!recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitment.getId());
    }

}
