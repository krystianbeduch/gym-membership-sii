package pl.krystianbeduch.gymmembership.membership.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class Money {

    @Column(name = "monthly_price_amount", nullable = false, precision = 10, scale = 2)
    // NotNull
    // @DecimalMin(value = "0.01"
    private BigDecimal amount;

    @Column(name = "monthly_price_currency", nullable = false, length = 3)
    // NotBlank
    // Size min 3 max 3
    private String currencyCode;

    public Currency getCurrency() {
        return Currency.getInstance(currencyCode);
    }

    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currencyCode = currency.getCurrencyCode();
    }
}
