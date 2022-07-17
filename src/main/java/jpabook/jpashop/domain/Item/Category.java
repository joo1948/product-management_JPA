package jpabook.jpashop.domain.Item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany//다대다는 가능한 사용 X JPA의 다대다 방식을 사용해보기 위하여 넣어보기
    @JoinTable(name="category_item",
        joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="item_id")
    )//SQL에서는 다대다인지 판단 X >> 중간필드를 생성하여 접근하기
    private List<Item> items = new ArrayList<>();


    //카테고리 구조는 계층구조임.
    //자신의 부모와 자식은 어떻게 확인하지 ? >> 하단의 방식 사용
    //자신의 엔티티에 부모와 자식을 매핑시켜줌
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy="parent")
    private List<Category> child = new ArrayList<>();//자식은 여러개가 될 수 있기 때문에 List 형식으로 사용

    //==연관관계 메서드==
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
