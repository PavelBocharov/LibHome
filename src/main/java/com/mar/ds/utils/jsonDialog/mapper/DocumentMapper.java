package com.mar.ds.utils.jsonDialog.mapper;

import com.mar.ds.db.entity.Document;
import com.mar.ds.db.entity.DocumentStatus;
import com.mar.ds.db.entity.DocumentType;
import com.mar.ds.utils.jsonDialog.jsonData.DocumentData;
import com.mar.ds.utils.jsonDialog.jsonData.DocumentListData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {

    default DocumentListData mappingDocuments(List<Document> documents, List<DocumentType> documentTypes) {
        return DocumentListData.builder()
                .documentDataList(mappingListDocument(documents))
                .documentTypeList(documentTypes)
                .build();
    }

    DocumentData mappingDocument(Document document);

    List<DocumentData> mappingListDocument(List<Document> document);

    default Long mappingDocumentStatus(DocumentStatus status) {
        if (status == null) return null;
        return status.getEnumId();
    }

    default Long mappingDocumentStatus(DocumentType type) {
        if (type == null) return null;
        return type.getId();
    }

}
