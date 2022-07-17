package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {


    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;


    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kang");

        //when
        Long saveId = memberService.join(member);

        //then
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));
        //@Transactional 어노테이션을 사용하였기 때문에
        //한 트랜잭션 안에 Equlas 비교를 하였을 때 맞으면 true 반환 해줄 것임

    }

    @Test
    public void 중복_회원_예외() throws Exception{

        //given
        Member member1 = new Member();
        member1.setName("kang1");

        Member member2 = new Member();
        member2.setName("kang1");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

    }


}





