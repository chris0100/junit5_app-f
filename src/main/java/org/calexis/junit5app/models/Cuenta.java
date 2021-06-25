package org.calexis.junit5app.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.calexis.junit5app.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;


    public void debito(BigDecimal monto){
       BigDecimal nuevoSaldo = this.saldo.subtract(monto);

       if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0){
           throw new DineroInsuficienteException("Dinero Insuficiente");
       }
       this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto){
        this.saldo = this.saldo.add(monto);
    }


}
