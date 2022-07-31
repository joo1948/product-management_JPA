package jpabook.jpashop.domain.Item;

import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Setter;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//간단하게 사용하기 위하여 SINGLE_TABLE 사용 >> DTYPE으로 구분 !
@DiscriminatorColumn(name="dtype")
public abstract class Item {

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy="items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==

    /**
     * stock 증가
     */
    public void aadStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

    /**
    * Item update
    **/
    public void change(String name, int price, int stockQuantity){
        this.name = name;
        this.price= price;
        this.stockQuantity = stockQuantity;
    }
}
