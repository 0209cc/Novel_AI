package com.cc.novel_ai.service;

import com.cc.novel_ai.dto.mapper.PromptMapper;
import com.cc.novel_ai.dto.request.PromptCreateRequest;
import com.cc.novel_ai.dto.response.PageResponse;
import com.cc.novel_ai.dto.response.PromptDetailResponse;
import com.cc.novel_ai.dto.response.PromptResponse;
import com.cc.novel_ai.entity.Prompt;
import com.cc.novel_ai.entity.User;
import com.cc.novel_ai.exception.BadRequestException;
import com.cc.novel_ai.exception.ResourceNotFoundException;
import com.cc.novel_ai.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提示词服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final UserService userService;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    /**
     * 创建提示词
     */
    @Transactional
    public PromptDetailResponse createPrompt(PromptCreateRequest request) {
        User currentUser = userService.getCurrentUser();

        Prompt prompt = Prompt.builder()
                .user(currentUser)
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .tags(request.getTags())
                .viewCount(0)
                .likeCount(0)
                .status(1)
                .build();

        prompt = promptRepository.save(prompt);

        log.info("Prompt created successfully: id={}, user={}", prompt.getId(), currentUser.getUsername());

        return PromptMapper.toDetailResponse(prompt);
    }

    /**
     * 获取提示词列表（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<PromptResponse> getPrompts(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Prompt> promptPage = promptRepository.findByStatusOrderByCreatedAtDesc(1, pageable);

        List<PromptResponse> prompts = promptPage.getContent().stream()
                .map(PromptMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<PromptResponse>builder()
                .content(prompts)
                .page(promptPage.getNumber())
                .size(promptPage.getSize())
                .totalElements(promptPage.getTotalElements())
                .totalPages(promptPage.getTotalPages())
                .build();
    }

    /**
     * 获取提示词详情
     */
    @Transactional
    public PromptDetailResponse getPromptById(Long id) {
        Prompt prompt = promptRepository.findByIdWithUser(id, 1)
                .orElseThrow(() -> new ResourceNotFoundException("Prompt", id));

        // 增加浏览量
        prompt.setViewCount(prompt.getViewCount() + 1);
        promptRepository.save(prompt);

        return PromptMapper.toDetailResponse(prompt);
    }

    /**
     * 更新提示词
     */
    @Transactional
    public PromptDetailResponse updatePrompt(Long id, PromptCreateRequest request) {
        Prompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prompt", id));

        // 验证权限
        User currentUser = userService.getCurrentUser();
        if (!prompt.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("只能编辑自己的提示词");
        }

        // 更新内容
        if (request.getTitle() != null) {
            prompt.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            prompt.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            prompt.setContent(request.getContent());
        }
        if (request.getTags() != null) {
            prompt.setTags(request.getTags());
        }

        prompt = promptRepository.save(prompt);

        log.info("Prompt updated successfully: id={}, user={}", prompt.getId(), currentUser.getUsername());

        return PromptMapper.toDetailResponse(prompt);
    }

    /**
     * 删除提示词（软删除）
     */
    @Transactional
    public void deletePrompt(Long id) {
        Prompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prompt", id));

        // 验证权限
        User currentUser = userService.getCurrentUser();
        if (!prompt.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("只能删除自己的提示词");
        }

        // 软删除
        prompt.setStatus(0);
        promptRepository.save(prompt);

        log.info("Prompt deleted successfully: id={}, user={}", prompt.getId(), currentUser.getUsername());
    }

    /**
     * 获取用户的提示词（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<PromptResponse> getUserPrompts(Long userId, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Prompt> promptPage = promptRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, 1, pageable);

        List<PromptResponse> prompts = promptPage.getContent().stream()
                .map(PromptMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<PromptResponse>builder()
                .content(prompts)
                .page(promptPage.getNumber())
                .size(promptPage.getSize())
                .totalElements(promptPage.getTotalElements())
                .totalPages(promptPage.getTotalPages())
                .build();
    }
}
