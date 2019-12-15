package com.brcxzam.sedapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Anexo_2_1")
public class Anexo21 {
    @PrimaryKey
    @NonNull private String id;
    private String periodo;
    private String fecha;
    private int s1_p1;
    private int s1_p2;
    private int s1_p3;
    private int s1_p4;
    private int s1_total_no;
    private int s1_total_si;
    private int s2_p1;
    private int s2_p2;
    private int s2_p3;
    private int s2_p4;
    private int s2_p5;
    private int s2_p6;
    private int s2_suma_no_cumple;
    private int s2_suma_parcialmente;
    private int s2_suma_cumple;
    private int s3_p1;
    private int s3_p2;
    private int s3_p3;
    private int s3_suma_no_cumple;
    private int s3_suma_parcialmente;
    private int s3_suma_cumple;
    private double total;
    private String aplicador;
    private String IEId;
    private String institucion_educativa;
    private String UERFC;
    private String razon_social;
    private String accion;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getS1_p1() {
        return s1_p1;
    }

    public void setS1_p1(int s1_p1) {
        this.s1_p1 = s1_p1;
    }

    public int getS1_p2() {
        return s1_p2;
    }

    public void setS1_p2(int s1_p2) {
        this.s1_p2 = s1_p2;
    }

    public int getS1_p3() {
        return s1_p3;
    }

    public void setS1_p3(int s1_p3) {
        this.s1_p3 = s1_p3;
    }

    public int getS1_p4() {
        return s1_p4;
    }

    public void setS1_p4(int s1_p4) {
        this.s1_p4 = s1_p4;
    }

    public int getS1_total_no() {
        return s1_total_no;
    }

    public void setS1_total_no(int s1_total_no) {
        this.s1_total_no = s1_total_no;
    }

    public int getS1_total_si() {
        return s1_total_si;
    }

    public void setS1_total_si(int s1_total_si) {
        this.s1_total_si = s1_total_si;
    }

    public int getS2_p1() {
        return s2_p1;
    }

    public void setS2_p1(int s2_p1) {
        this.s2_p1 = s2_p1;
    }

    public int getS2_p2() {
        return s2_p2;
    }

    public void setS2_p2(int s2_p2) {
        this.s2_p2 = s2_p2;
    }

    public int getS2_p3() {
        return s2_p3;
    }

    public void setS2_p3(int s2_p3) {
        this.s2_p3 = s2_p3;
    }

    public int getS2_p4() {
        return s2_p4;
    }

    public void setS2_p4(int s2_p4) {
        this.s2_p4 = s2_p4;
    }

    public int getS2_p5() {
        return s2_p5;
    }

    public void setS2_p5(int s2_p5) {
        this.s2_p5 = s2_p5;
    }

    public int getS2_p6() {
        return s2_p6;
    }

    public void setS2_p6(int s2_p6) {
        this.s2_p6 = s2_p6;
    }

    public int getS2_suma_no_cumple() {
        return s2_suma_no_cumple;
    }

    public void setS2_suma_no_cumple(int s2_suma_no_cumple) {
        this.s2_suma_no_cumple = s2_suma_no_cumple;
    }

    public int getS2_suma_parcialmente() {
        return s2_suma_parcialmente;
    }

    public void setS2_suma_parcialmente(int s2_suma_parcialmente) {
        this.s2_suma_parcialmente = s2_suma_parcialmente;
    }

    public int getS2_suma_cumple() {
        return s2_suma_cumple;
    }

    public void setS2_suma_cumple(int s2_suma_cumple) {
        this.s2_suma_cumple = s2_suma_cumple;
    }

    public int getS3_p1() {
        return s3_p1;
    }

    public void setS3_p1(int s3_p1) {
        this.s3_p1 = s3_p1;
    }

    public int getS3_p2() {
        return s3_p2;
    }

    public void setS3_p2(int s3_p2) {
        this.s3_p2 = s3_p2;
    }

    public int getS3_p3() {
        return s3_p3;
    }

    public void setS3_p3(int s3_p3) {
        this.s3_p3 = s3_p3;
    }

    public int getS3_suma_no_cumple() {
        return s3_suma_no_cumple;
    }

    public void setS3_suma_no_cumple(int s3_suma_no_cumple) {
        this.s3_suma_no_cumple = s3_suma_no_cumple;
    }

    public int getS3_suma_parcialmente() {
        return s3_suma_parcialmente;
    }

    public void setS3_suma_parcialmente(int s3_suma_parcialmente) {
        this.s3_suma_parcialmente = s3_suma_parcialmente;
    }

    public int getS3_suma_cumple() {
        return s3_suma_cumple;
    }

    public void setS3_suma_cumple(int s3_suma_cumple) {
        this.s3_suma_cumple = s3_suma_cumple;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getAplicador() {
        return aplicador;
    }

    public void setAplicador(String aplicador) {
        this.aplicador = aplicador;
    }

    public String getIEId() {
        return IEId;
    }

    public void setIEId(String IEId) {
        this.IEId = IEId;
    }

    public String getInstitucion_educativa() {
        return institucion_educativa;
    }

    public void setInstitucion_educativa(String institucion_educativa) {
        this.institucion_educativa = institucion_educativa;
    }

    public String getUERFC() {
        return UERFC;
    }

    public void setUERFC(String UERFC) {
        this.UERFC = UERFC;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String action) {
        this.accion = action;
    }
}
