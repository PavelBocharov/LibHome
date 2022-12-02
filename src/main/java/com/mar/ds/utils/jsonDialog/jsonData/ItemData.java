package com.mar.ds.utils.jsonDialog.jsonData;

import com.mar.ds.db.entity.ArtifactEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemData implements Serializable {

    private Long id;
    private String name;
    private String info;
    private String shortInfo;
    private Long status;
    private Long type;
    private String imgPath;
    private Float needManna;
    private Float healthDamage;
    private Float mannaDamage;
    private Float reloadTick;
    private String objPath;
    private String level;
    private Float positionX;
    private Float positionY;
    private Float positionZ;
    private Float rotationX;
    private Float rotationY;
    private Float rotationZ;
    private ArtifactEffect artifactEffect;
}
