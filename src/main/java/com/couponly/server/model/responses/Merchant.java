package com.couponly.server.model.responses;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "merchants")
public class Merchant {
    @Id
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("locality")
    private String locality;
    @SerializedName("region")
    private String region;
    @SerializedName("postal_code")
    private String postalCode;
    @SerializedName("country")
    private String country;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("phone_number")
    private String phoneNumber;
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "merchants")
//    private List<Deal> deals;
}
