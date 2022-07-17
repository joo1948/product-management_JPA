package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    //값 타입은 @Setter를 사용하지 않고 생성자를 통하여 접근하도록 하자
    //기본 생성자는 필수이지만, public 으로 해두지 말고 protected로 두어서 다른 사람이 소스를 확인했을 때 기본생성자가 아닌
    //생성자를 통하여 접근해야하는 구나 라고 생각할 수 있게 한다.
}
