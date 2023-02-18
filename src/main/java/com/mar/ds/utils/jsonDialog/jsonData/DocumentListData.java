package com.mar.ds.utils.jsonDialog.jsonData;

import com.mar.ds.db.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListData implements Serializable {

    private List<DocumentType> documentTypeList;
    private List<DocumentData> documentDataList;

}
