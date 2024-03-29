package com.wuju.gmall;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class BaseCatalog1 implements Serializable {
    @Column
    @Id
    private String id;
    @Column
    private String name;
}
