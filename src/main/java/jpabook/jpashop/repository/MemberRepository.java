package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository//spring been으로 사용하게됨
@RequiredArgsConstructor
public class MemberRepository {


    private final EntityManager em;
    /*
    * 원래 EntityManager 는  @PersistenceContext를 사용해주어야 함.
    * 하지만 spring boot 라이브러리르 쓰게 되는 경우 해당 부분을 @Autowired로 변경하여 사용가능
    * == 생성자를 만들어서 사용 가능 == final을 붙힌 변수에 대하여 생성자를 기본으로 만들어 주는 어노테이션을 사용@(RequiredArgsConstructor)
    */

    public void save(Member member){
        em.persist(member);
        //persist 하는 순간에 영속성 컨테스트에 들어감.
    }

    public Member findOne(Long id){//단건조회
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",
                        Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
