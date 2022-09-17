package server.answer.controller;


import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.answer.dto.AnswerPostPutDto;
import server.answer.dto.VotesDto;
import server.answer.entity.Answer;
import server.answer.mapper.AnswerMapper;
import server.answer.service.AnswerService;
import server.comment.service.CommentService;
import server.question.service.QuestionService;
import server.user.mapper.UserMapper;
import server.user.service.UserService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@RestController
public class AnswerController {

    private final AnswerService answerService;
    private final AnswerMapper answerMapper;
    private final QuestionService questionService;
    private final UserService userService;

    private final CommentService commentService;
    private final UserMapper userMapper;


    @PostMapping("/questions/{question-id}/answers")
    public ResponseEntity postAnswer(@Positive @PathVariable("question-id") long questionId,
                                     @Valid @RequestBody AnswerPostPutDto answerPostPutDto) throws Exception {
        // TODO: 로그인 유저 정보 가져오기
        long userId = 1L;
        Answer answer = answerMapper.answerPostPutDtoToAnswer(answerPostPutDto);
        answer.setQuestion(questionService.findVerifiedQuestion(questionId));
        answer.setUser(userService.findUser(userId));

        answerService.createdAnswer(answer);

        return new ResponseEntity("답변 작성이 완료되었습니다.", HttpStatus.CREATED);
    }

    @PutMapping("/answers/{answer-id}")
    public ResponseEntity putAnswer(@Positive @PathVariable("answer-id") long answerId,
                                    @Valid @RequestBody AnswerPostPutDto answerPostPutDto) throws Exception {
        Answer answer = answerMapper.answerPostPutDtoToAnswer(answerPostPutDto);
        answer.setAnswerId(answerId);
        answerService.updateAnswer(answer);
        return new ResponseEntity("답변 수정이 완료되었습니다.", HttpStatus.OK);
    }

    @PutMapping("/answers/{answer-id}/votes")
    public ResponseEntity putVotes(@Positive @PathVariable("answer-id") long answerId,
                                   @Valid @RequestBody VotesDto votesDto) throws Exception {
        answerService.updateVotes(answerId, votesDto.getVotes());
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/answers/{answer-id}")
    public ResponseEntity deleteAnswer(@Positive @PathVariable("answer-id") long answerId) throws Exception {
        answerService.deleteAnswer(answerId);
        return new ResponseEntity("답변이 삭제되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/test/{answer-id}")
    public ResponseEntity test(@PathVariable("answer-id") long answerId) throws Exception {
        Answer answer = answerService.findVerifiedAnswer(answerId);
        return new ResponseEntity(answerMapper.answerToAnswerResponseDto(answer, userMapper, commentService), HttpStatus.OK);
    }
}
