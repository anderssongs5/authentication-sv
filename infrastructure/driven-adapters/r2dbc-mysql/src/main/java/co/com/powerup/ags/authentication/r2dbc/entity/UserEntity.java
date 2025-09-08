package co.com.powerup.ags.authentication.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity implements Persistable<String> {

    @Id
    @Column("id")
    private String id;
    
    @Column("name")
    private String name;
    
    @Column("last_name")
    private String lastName;
    
    @Column("address")
    private String address;
    
    @Column("phone_number")
    private String phoneNumber;
    
    @Column("birth_date")
    private LocalDate birthDate;
    
    @Column("email")
    private String email;
    
    @Column("base_salary")
    private BigDecimal baseSalary;
    
    @Column("identity_number")
    private String idNumber;
    
    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}