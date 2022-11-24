package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Document;
import com.mar.ds.db.entity.DocumentStatus;
import com.mar.ds.utils.jsonDialog.jsonData.DocumentData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {


    DocumentData mappingDocument(Document document);

    List<DocumentData> mappingDocuments(List<Document> document);

    default Long mappingDocumentStatus(DocumentStatus status) {
        if (status == null) return null;
        return status.getEnumId();
    }

}
