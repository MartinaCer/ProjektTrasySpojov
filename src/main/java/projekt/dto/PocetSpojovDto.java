/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekt.dto;

/**
 *
 * @author Ja
 */
public class PocetSpojovDto {

    private final int spojeCelkovo;
    private final int pocetPrepojeni;

    public PocetSpojovDto(int spojeCelkovo, int pocetPrepojeni) {
        this.spojeCelkovo = spojeCelkovo;
        this.pocetPrepojeni = pocetPrepojeni;
    }

    public int getSpojeCelkovo() {
        return spojeCelkovo;
    }

    public int getPocetPrepojeni() {
        return pocetPrepojeni;
    }

}
