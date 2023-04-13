package com.vpp97.spring_2_testing.models;

import com.vpp97.spring_2_testing.exceptions.NotEnoughMoneyException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="owner_name")
    private String ownerName;
    private BigDecimal balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;

        if (getId() != null ? !getId().equals(account.getId()) : account.getId() != null) return false;
        if (getOwnerName() != null ? !getOwnerName().equals(account.getOwnerName()) : account.getOwnerName() != null)
            return false;
        return getBalance() != null ? getBalance().equals(account.getBalance()) : account.getBalance() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getOwnerName() != null ? getOwnerName().hashCode() : 0);
        result = 31 * result + (getBalance() != null ? getBalance().hashCode() : 0);
        return result;
    }

    public void debit(BigDecimal amount){
        BigDecimal newBalance = this.balance.subtract(amount);

        if(newBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new NotEnoughMoneyException("Not enough money on this account");
        }

        this.balance = newBalance;
    }

    public void credit(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }
}
