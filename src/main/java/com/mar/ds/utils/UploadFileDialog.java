package com.mar.ds.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

@Slf4j
public class UploadFileDialog extends Dialog {

    private Upload uploadFile;

    private String rootDir;
    private int countFiles;
    private boolean isCover;

    public UploadFileDialog(String rootDir, boolean isCover, int countFiles, Runnable afterUploadEvent) {
        this.rootDir = rootDir;
        this.countFiles = countFiles;
        this.isCover = isCover;

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setHeight(400, Unit.PIXELS);
        dialog.setWidth(600, Unit.PIXELS);
        dialog.setMaxHeight(50, Unit.PERCENTAGE);
        dialog.setMaxWidth(50, Unit.PERCENTAGE);

        Icon icon = new Icon(BAN);
        icon.setColor("red");
        Button noBtn = new Button("Close", icon);
        noBtn.addClickListener(btnEvent -> {
            dialog.close();
        });
        noBtn.setWidthFull();

        Upload upload = initAndGetUploadView();
        upload.addSucceededListener(event -> {
            afterUploadEvent.run();
            dialog.close();
        });

        dialog.add(new Text("Upload file"), upload, noBtn);
        dialog.open();
    }

    private Upload initAndGetUploadView() {
        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        uploadFile = new Upload(multiFileMemoryBuffer);

        uploadFile.addSucceededListener(event -> {
            InputStream fileData = multiFileMemoryBuffer.getInputStream(event.getFileName());
            String uploadFileName = isCover
                    ? "cover." + FilenameUtils.getExtension(event.getFileName())
                    : UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(event.getFileName());
            int contentLength = (int) event.getContentLength();
            String mimeType = event.getMIMEType();

            // Do something with the file data
            // processFile(fileData, fileName, contentLength, mimeType);

            try {
                BufferedInputStream bis = new BufferedInputStream(fileData);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (int result = bis.read(); result != -1; result = bis.read()) {
                    buf.write((byte) result);
                }
                byte[] uploadFileData = buf.toByteArray();

                log.debug("SucceededListener --> filename: {}, size: {} byte, MIME: {}, byteLength: {}", uploadFileName, contentLength, mimeType, uploadFileData.length);

                FileUtils.writeByteArrayToFile(
                        new File(this.rootDir + uploadFileName),
                        uploadFileData
                );
                fileData.close();
                buf.close();
                bis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // TODO: SEND ERROR MSG
        uploadFile.addFileRejectedListener(fileRejectedEvent -> {
                    log.debug("FileRejectedListener -->  {}", fileRejectedEvent.getErrorMessage());
                    ViewUtils.showErrorMsg("Send post exception: ", new Exception(fileRejectedEvent.getErrorMessage()));
                }
        );
        uploadFile.addFailedListener(failedEvent -> log.warn("FailedListener --> {}", failedEvent.getReason().getMessage()));
//        uploadFile.addStartedListener(event -> log.debug("StartedListener --> filename: {}, MIME: {}", event.getFileName(), event.getMIMEType()));
//        uploadFile.addProgressListener(progressUpdateEvent -> log.debug("ProgressListener: --> length: {}", progressUpdateEvent.getContentLength()));

        String maxFileSize = PropertiesLoader
                .loadProperties("application.properties")
                .getProperty("spring.servlet.multipart.max-file-size", "10MB");
        Integer fileSize = Integer.parseInt(maxFileSize.substring(0, maxFileSize.length() - 2)) * 1024 * 1024;
        uploadFile.setMaxFileSize(fileSize);
        uploadFile.setAcceptedFileTypes(
                IMAGE_JPEG_VALUE,
                IMAGE_PNG_VALUE
        );

        uploadFile.setWidth(90, Unit.PERCENTAGE);
        uploadFile.setHeight(75, Unit.PERCENTAGE);
        Button uploadBtn = new Button("Upload file...", new Icon(VaadinIcon.UPLOAD));
        uploadBtn.setWidthFull();
        uploadFile.setUploadButton(uploadBtn);
        uploadFile.setAutoUpload(true);
        uploadFile.setMaxFiles(countFiles);
        uploadFile.setDropAllowed(true);
        return uploadFile;
    }

}
