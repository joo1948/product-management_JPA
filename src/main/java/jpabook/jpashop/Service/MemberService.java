package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // final을 가지고 있는 애를 가지고 생성자를 생성해줌
public class MemberService {
    private final MemberRepository memberRepository;

    /*
    * >> 현재 @RequiredArgsConstructor를 사용하여 생성자 자동 생성해주기 때문에
    * >> 제거
    *
    @Autowired//Autowired >> spring이 memberRepository를 인젝션 해준다
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        //memberRepository 에 값을 주입하기가 힘들다.
        // setter로 주입해주면 애플리케이션이 동작하면서 값이 변경될 수 있음 >> 생성자로 설정하여 초기 한 번만 해주는 것으로 설정
        //생성자로 해두면 memberRepository는 초기 한 번 적용되는 것이기 때문에 final 사용함.
        * >> 따라서 @RequiredArgsConstructor를 사용하여 생성자 자동 생성해주기 때문에
    }
    */

    //회원가입
    @Transactional
    public Long join(Member member){
        //Validation 체크
        validateDuplicateMember(member);

        //회원등록
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }


    //회원 단건 조회
    public Member findMember(Long memberId){
        return memberRepository.findOne(memberId);
    }

}
