package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class EyeTrackingTestContentPairs extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_PAIR_1_PART_1 = "pair_1_part_1";
    public static final String FILED_PAIR_1_PART_2 = "pair_1_part_2";
    public static final String FILED_PAIR_2_PART_1 = "pair_2_part_1";
    public static final String FILED_PAIR_2_PART_2 = "pair_2_part_2";
    public static final String FILED_PAIR_3_PART_1 = "pair_3_part_1";
    public static final String FILED_PAIR_3_PART_2 = "pair_3_part_2";
    public static final String FILED_PAIR_4_PART_1 = "pair_4_part_1";
    public static final String FILED_PAIR_4_PART_2 = "pair_4_part_2";
    public static final String FILED_PAIR_5_PART_1 = "pair_5_part_1";
    public static final String FILED_PAIR_5_PART_2 = "pair_5_part_2";
    public static final String FILED_PAIR_6_PART_1 = "pair_6_part_1";
    public static final String FILED_PAIR_6_PART_2 = "pair_6_part_2";
    public static final String FILED_PAIR_7_PART_1 = "pair_7_part_1";
    public static final String FILED_PAIR_7_PART_2 = "pair_7_part_2";
    public static final String FILED_PAIR_8_PART_1 = "pair_8_part_1";
    public static final String FILED_PAIR_8_PART_2 = "pair_8_part_2";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_IS_SELECTED = "is_deleted";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_NAME)
    private String name;

    @SerializedName(FILED_PAIR_1_PART_1)
    private String pair1part1;

    @SerializedName(FILED_PAIR_1_PART_2)
    private String pair1part2;

    @SerializedName(FILED_PAIR_2_PART_1)
    private String pair2part1;

    @SerializedName(FILED_PAIR_2_PART_2)
    private String pair2part2;

    @SerializedName(FILED_PAIR_3_PART_1)
    private String pair3part1;

    @SerializedName(FILED_PAIR_3_PART_2)
    private String pair3part2;

    @SerializedName(FILED_PAIR_4_PART_1)
    private String pair4part1;

    @SerializedName(FILED_PAIR_4_PART_2)
    private String pair4part2;

    @SerializedName(FILED_PAIR_5_PART_1)
    private String pair5part1;

    @SerializedName(FILED_PAIR_5_PART_2)
    private String pair5part2;

    @SerializedName(FILED_PAIR_6_PART_1)
    private String pair6part1;

    @SerializedName(FILED_PAIR_6_PART_2)
    private String pair6part2;
    @SerializedName(FILED_PAIR_7_PART_1)
    private String pair7part1;

    @SerializedName(FILED_PAIR_7_PART_2)
    private String pair7part2;

    @SerializedName(FILED_PAIR_8_PART_1)
    private String pair8part1;

    @SerializedName(FILED_PAIR_8_PART_2)
    private String pair8part2;

    @SerializedName(FILED_ADD_DATETIME)
    private long addDateTime;

    @SerializedName(FILED_ADD_BY)
    private String addBy;

    @SerializedName(FILED_MOD_DATETIME)
    private long modDateTime;

    @SerializedName(FILED_MOD_BY)
    private String modBy;

    @SerializedName(FILED_IS_SELECTED)
    private String isDeleted;

    public EyeTrackingTestContentPairs() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPair1part1() {
        return pair1part1;
    }

    public void setPair1part1(String pair1part1) {
        this.pair1part1 = pair1part1;
    }

    public String getPair1part2() {
        return pair1part2;
    }

    public void setPair1part2(String pair1part2) {
        this.pair1part2 = pair1part2;
    }

    public String getPair2part1() {
        return pair2part1;
    }

    public void setPair2part1(String pair2part1) {
        this.pair2part1 = pair2part1;
    }

    public String getPair2part2() {
        return pair2part2;
    }

    public void setPair2part2(String pair2part2) {
        this.pair2part2 = pair2part2;
    }

    public String getPair3part1() {
        return pair3part1;
    }

    public void setPair3part1(String pair3part1) {
        this.pair3part1 = pair3part1;
    }

    public String getPair3part2() {
        return pair3part2;
    }

    public void setPair3part2(String pair3part2) {
        this.pair3part2 = pair3part2;
    }

    public String getPair4part1() {
        return pair4part1;
    }

    public void setPair4part1(String pair4part1) {
        this.pair4part1 = pair4part1;
    }

    public String getPair4part2() {
        return pair4part2;
    }

    public void setPair4part2(String pair4part2) {
        this.pair4part2 = pair4part2;
    }

    public String getPair5part1() {
        return pair5part1;
    }

    public void setPair5part1(String pair5part1) {
        this.pair5part1 = pair5part1;
    }

    public String getPair5part2() {
        return pair5part2;
    }

    public void setPair5part2(String pair5part2) {
        this.pair5part2 = pair5part2;
    }

    public String getPair6part1() {
        return pair6part1;
    }

    public void setPair6part1(String pair6part1) {
        this.pair6part1 = pair6part1;
    }

    public String getPair6part2() {
        return pair6part2;
    }

    public void setPair6part2(String pair6part2) {
        this.pair6part2 = pair6part2;
    }

    public String getPair7part1() {
        return pair7part1;
    }

    public void setPair7part1(String pair7part1) {
        this.pair7part1 = pair7part1;
    }

    public String getPair7part2() {
        return pair7part2;
    }

    public void setPair7part2(String pair7part2) {
        this.pair7part2 = pair7part2;
    }

    public String getPair8part1() {
        return pair8part1;
    }

    public void setPair8part1(String pair8part1) {
        this.pair8part1 = pair8part1;
    }

    public String getPair8part2() {
        return pair8part2;
    }

    public void setPair8part2(String pair8part2) {
        this.pair8part2 = pair8part2;
    }

    public long getAddDateTime() {
        return addDateTime;
    }

    public void setAddDateTime(long addDateTime) {
        this.addDateTime = addDateTime;
    }

    public String getAddBy() {
        return addBy;
    }

    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }

    public long getModDateTime() {
        return modDateTime;
    }

    public void setModDateTime(long modDateTime) {
        this.modDateTime = modDateTime;
    }

    public String getModBy() {
        return modBy;
    }

    public void setModBy(String modBy) {
        this.modBy = modBy;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
}
