package com.mar.ds.utils;

import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.imageio.ImageIO;

import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

@Slf4j
public class UploadFileDialog extends Dialog {

    private MainView mainView;
    private Upload uploadFile;

    private String rootDir;
    private int countFiles;
    private boolean isCover;

    public UploadFileDialog(MainView mainView, String rootDir, boolean isCover, int countFiles, Runnable afterUploadEvent) {
        this.mainView = mainView;
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
            String formatName = FilenameUtils.getExtension(event.getFileName());
            String uploadFileName = isCover
                    ? "cover." + FilenameUtils.getExtension(event.getFileName())
                    : UUID.randomUUID().toString().replace("-", "") + "." + formatName;
            int contentLength = (int) event.getContentLength();
            String mimeType = event.getMIMEType();

            try {
                BufferedInputStream bis = new BufferedInputStream(fileData);

                BufferedImage inBufImg = ImageIO.read(bis);

                int h = inBufImg.getHeight();
                int maxH = Integer.parseInt(mainView.getEnv().getProperty("image.max.height", "600"));
                int newW = inBufImg.getWidth();
                int newH = inBufImg.getHeight();

                if (h > maxH) {
                    newH = maxH;
                    newW = inBufImg.getWidth() * maxH / h;
                }

                BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.drawImage(inBufImg, 0, 0, newW, newH, null);
                graphics2D.dispose();

                ImageIO.write(
                        resizedImage,
                        formatName,
                        new File(this.rootDir + uploadFileName)
                );

                fileData.close();
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

        String maxFileSize = mainView.getEnv().getProperty("spring.servlet.multipart.max-file-size", "10MB");
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
