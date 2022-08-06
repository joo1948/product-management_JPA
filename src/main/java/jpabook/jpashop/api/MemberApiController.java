package jpabook.jpashop.api;

import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberservice;


    @GetMapping("/api/v1/members")
    public List<Member> memberV1() { // 엔티티 자체를 반환하는 것은 좋지 않음
        return memberservice.findMembers();
    }

    //회원 정보 조회 API >> 단순 name만 있는 것 + id
    @GetMapping("/api/v2/members")
    public Result memberV2() { // 엔티티 자체를 반환하는 것은 좋지 않음
        List<Member> findMembers = memberservice.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getId(), m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);

    }

    @Data
    @AllArgsConstructor
    static class MemberDto{ //단순 이름정보만 조회함. + id
        private Long id;
        private String name;

    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //parameter로 엔티티를 주고받는건 굉장히 위험. >> DTO등을 사용하자
        Long id = memberservice.join(member);
        return new CreateMemberResponse(id);
    }

    //회원 정보 등록 API
    /**
     * API는 절대 엔티티를 노출 시키지 말고 항상  DTO(객체 등)를 사용하자
     * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberservice.join(member);
        return new CreateMemberResponse(id);
    }

    //회원 정보 수정 API
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

            memberservice.update(id, request.getName());//update는 단순 update만 ! 따로 반환값으로 엔티티를 주지 말자
            Member findMember = memberservice.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
     static class CreateMemberRequest{
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
