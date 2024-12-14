package com.viettran.reading_story_web.service;

import com.viettran.reading_story_web.dto.request.RatingRequest;
import com.viettran.reading_story_web.dto.request.StoryRequest;
import com.viettran.reading_story_web.dto.response.ChapterResponse;
import com.viettran.reading_story_web.dto.response.FollowResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.dto.response.StoryResponse;
import com.viettran.reading_story_web.entity.redis.StoryCache;
import com.viettran.reading_story_web.entity.mysql.Chapter;
import com.viettran.reading_story_web.entity.mysql.Follow;
import com.viettran.reading_story_web.entity.mysql.Genre;
import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.enums.Gender;
import com.viettran.reading_story_web.enums.StoryStatus;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.ChapterMapper;
import com.viettran.reading_story_web.mapper.StoryMapper;
import com.viettran.reading_story_web.mapper.UserMapper;
import com.viettran.reading_story_web.repository.ChapterRepository;
import com.viettran.reading_story_web.repository.FollowRepository;
import com.viettran.reading_story_web.repository.StoryCacheRepository;
import com.viettran.reading_story_web.repository.StoryRepository;
import com.viettran.reading_story_web.scheduler.StoryJobScheduler;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryService {
    StoryRepository storyRepository;
    FollowRepository followRepository;
    ChapterRepository chapterRepository;

    StoryMapper storyMapper;
    ChapterMapper chapterMapper;

    FileService fileService;
    GenreService genreService;

    DateTimeFormatUtil dateTimeFormatUtil;

    StoryCacheRepository storyCacheRepository;

    StoryJobScheduler storyJobScheduler;
    AuthenticationService authenticationService;

    @NonFinal
    @Value("${app.folder.story}")
    protected String STORY_FOLDER;

    @PreAuthorize("hasRole('ADMIN')")
    public StoryResponse createStory(StoryRequest request) throws IOException {
        String imgSrc = fileService.uploadFile(request.getFile(), STORY_FOLDER);

        Story story = storyMapper.toStory(request);
        Set<Genre> genres =  genreService.getGenresByIds(request.getGenreIds());

        story.setImgSrc(imgSrc);
        story.setGenres(genres);
        story.setCreatedAt(Instant.now());
        story.setUpdatedAt(Instant.now());

        StoryResponse response = storyMapper.toStoryResponse(storyRepository.save(story));
        response.setStatus(StoryStatus.getFullNameFromStatus(story.getStatus()));
        return response;
    }


    public PageResponse<StoryResponse> getStories(int page, int size){
        Sort sort = Sort.by("updatedAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = storyRepository.findAll(pageable);
        Collections.shuffle(pageData.getContent());// xáo trộn list trả về

        List<StoryResponse> storyResponseList = pageData.getContent().stream().map(result -> {
            List<Chapter > chapters = chapterRepository.findTop3ChaptersByStoryId(result.getId());

            StoryResponse storyResponse = storyMapper.toStoryResponse(result);
            storyResponse.setChapters(chapters.stream().map(chapter-> {

                ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
                chapterResponse.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));
                chapterResponse.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));

                return  chapterResponse;
            }).toList());

            return storyResponse;
        }).toList();

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(storyResponseList)
                .build();
    }


    public StoryResponse getStoryById(Integer storyId){
        Story story = storyRepository.findById(storyId)
                .orElseThrow(()-> new AppException(ErrorCode.STORY_NOT_EXISTED));
        StoryResponse response = storyMapper.toStoryResponse(story);

        response.setUpdatedAt(dateTimeFormatUtil.format(story.getUpdatedAt()));
        response.setCreatedAt(dateTimeFormatUtil.format(story.getCreatedAt()));
        response.setStatus(StoryStatus.getFullNameFromStatus(story.getStatus()));

        return  response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStory(Integer id){
        storyRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public StoryResponse updateStory(StoryRequest request, Integer storyId){
        Story story = storyRepository.findById(storyId)
                .orElseThrow(()-> new AppException(ErrorCode.STORY_NOT_EXISTED));

        storyMapper.updateStory(story, request);

        return storyMapper.toStoryResponse(storyRepository.save(story));
    }


    public PageResponse<StoryResponse> searchStories(String keyword, int page, int size){
        Sort sort = Sort.by("updatedAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = storyRepository.searchByKeyword(keyword, pageable);

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream().map(storyMapper::toStoryResponse).toList())
                .build();
    }
    // id bằng 1 đang lỗi
    public StoryResponse rateStory(int storyId, RatingRequest request){
        if (request.getPoint() < 1 || request.getPoint() > 5) {
            throw new AppException(ErrorCode.RATING_POINT_INVALID);
        }
        Story story = storyRepository.findById(storyId)
                .orElseThrow(()-> new AppException(ErrorCode.STORY_NOT_EXISTED));
        // làm tròn
        DecimalFormat df = new DecimalFormat("#.##");
        double roundedNumber = Double.parseDouble(df.format((story.getRate() + request.getPoint())/ story.getRatingCount()));

        story.setRatingCount(story.getRatingCount() + 1);
        story.setRate(roundedNumber);

        storyRepository.save(story);
        return storyMapper.toStoryResponse(story);
    }


    public PageResponse<StoryResponse> getStoriesByGenreQueryCode(
            Integer queryCode,
            StoryStatus status,
            int sort,
            int page,
            int size
    ) {
        Sort sorting = Sort.by(Sort.Direction.DESC, sort == 2 ? "createdAt" : "updatedAt");

        Pageable pageable = PageRequest.of(page, size, sorting);

        // Gọi repository
        var pageData =  storyRepository.findStoriesByGenreQueryCodeAndFilters(queryCode, status, pageable);
        Collections.shuffle(pageData.getContent());


        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream().map(storyMapper::toStoryResponse).toList())
                .build();
    }


    public PageResponse<StoryResponse> getStoriesByGender(int page, int size,Gender gender){
        Sort sorting = Sort.by(Sort.Direction.DESC, "updatedAt");

        Pageable pageable = PageRequest.of(page - 1, size, sorting);
        // theo giới tính: name, nữ, cả hai
        // => tìm truyện dành cho nam => không tìm dành cho nữ
        var pageData =  storyRepository.findByGenderNot(gender, pageable);
        Collections.shuffle(pageData.getContent());

        List<StoryResponse> storyResponseList = pageData.getContent().stream().map(result -> {
            List<Chapter > chapters = chapterRepository.findTop3ChaptersByStoryId(result.getId());

            StoryResponse storyResponse = storyMapper.toStoryResponse(result);
            storyResponse.setChapters(chapters.stream().map(chapter-> {

                ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
                chapterResponse.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));
                chapterResponse.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));

                return  chapterResponse;
            }).toList());

            return storyResponse;
        }).toList();

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(storyResponseList)
                .build();
    }

    public PageResponse<StoryResponse> getHotStories(int page, int size){
        Sort sorting = Sort.by(Sort.Direction.DESC, "updatedAt");

        Pageable pageable = PageRequest.of(page, size, sorting);

        var pageData =  storyRepository.findByHotTrue(pageable);
        Collections.shuffle(pageData.getContent());

        List<StoryResponse> storyResponseList = pageData.getContent().stream().map(result -> {
            List<Chapter > chapters = chapterRepository.findTop3ChaptersByStoryId(result.getId());

            StoryResponse storyResponse = storyMapper.toStoryResponse(result);
            storyResponse.setChapters(chapters.stream().map(chapter-> {

                ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
                chapterResponse.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));
                chapterResponse.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));

                return  chapterResponse;
            }).toList());

            return storyResponse;
        }).toList();

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(storyResponseList)
                .build();
    }

    public List<StoryResponse> getTop10StoriesByViewCount() {
        //Iterable<StoryCache> iterable = storyCacheRepository.findAll();
        Pageable pageable = PageRequest.of(0, 10);
        List<Story> topStories  = storyRepository.findTop10ByOrderByViewCountDesc(pageable);

        return topStories.stream().map(storyMapper::toStoryResponse).toList();

       /* if(!iterable.iterator().hasNext()){
            // cache không thấy


            for(Story story : topStories){
                // lưu cache
                storyCacheRepository.save(storyMapper.toStoryCache(story));
            }

        }else{
            List<StoryCache> cacheTopStories = new ArrayList<>();
            iterable.forEach(cacheTopStories::add);

            return cacheTopStories.stream().map(storyMapper::toStoryResponse).toList();
        }*/
    }


    public List<FollowResponse> getFollowedStories(){
        String userId = authenticationService.getCurrentUserId();
        List<Follow> followedStories= followRepository.findFollowedStories(userId);

        return followedStories.stream()
                .map(follow -> FollowResponse.builder()
                        .id(follow.getId())
                        .story(storyMapper.toStoryResponse(follow.getStory()))
                        .followTime(dateTimeFormatUtil.format(follow.getFollowTime()))
                        .build())
                .toList();

    }

    public FollowResponse getFollowedStoryByUserIdAndStoryId(int storyId){
        String userId = authenticationService.getCurrentUserId();

        Optional<Follow> followOptional = followRepository.findByUserIdAndStoryId(userId, storyId);
        if(followOptional.isEmpty())
            throw  new AppException(ErrorCode.FOLLOWED_STORY);
        Follow follow = followOptional.get();

        return FollowResponse
                .builder()
                .id(follow.getId())
                .story(storyMapper.toStoryResponse(follow.getStory()))
                .followTime(dateTimeFormatUtil.format(follow.getFollowTime()))
                .build();
    }

    @PostConstruct
    public void cacheStoryViewCountInitially() {
       storyJobScheduler.syncDataFromRedisToMySQL();
       storyJobScheduler.cacheStoryViewCountInRedis();
    }

}
