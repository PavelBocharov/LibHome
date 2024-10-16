package com.mar.ds.utils;

import com.mar.ds.db.entity.Card;
import com.mar.ds.views.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;

import static com.vaadin.flow.component.icon.VaadinIcon.BAN;
import static java.lang.String.valueOf;

@Slf4j
public class UploadFileDialog extends Dialog {

    public static final List<String> nameWordExc = List.of("\\", "/", ":", "*", "?", "\"", "<", ">", "|", "+", " ");

    private MainView mainView;
    private Upload uploadFile;

    private final Card card;
    private final String rootDir;
    private final int countFiles;
    private final boolean isCover;
    private final Set<String> uploadFileTypes;

    public UploadFileDialog(MainView mainView, String rootDir, Card card, boolean isCover, int countFiles, Set<String> uploadFileTypes, Runnable afterUploadEvent) {
        this.mainView = mainView;
        this.card = card;
        this.rootDir = rootDir;
        this.countFiles = countFiles;
        this.isCover = isCover;
        this.uploadFileTypes = uploadFileTypes;

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
        checkRootDir();
        log.info("Init upload view");
        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        uploadFile = new Upload(multiFileMemoryBuffer);
        uploadFile.addSucceededListener(event -> {
            log.info("success upload listener: {}", event.getFileName());
            try (InputStream fileData = multiFileMemoryBuffer.getInputStream(event.getFileName())) {
                String formatName = FilenameUtils.getExtension(event.getFileName()).toLowerCase();
                if (formatName.endsWith("jpg") || formatName.endsWith("jpeg") || formatName.endsWith("png")) {
                    uploadImage(fileData, event.getFileName());
                } else {
                    uploadFile(fileData, event.getFileName());
                }
            } catch (IOException e) {
                ViewUtils.showErrorMsg("Cannot upload file", e);
                throw new RuntimeException(e);
            }
        });

        // TODO: SEND ERROR MSG
        uploadFile.addFileRejectedListener(fileRejectedEvent -> {
                    log.warn("FileRejectedListener -->  {}", fileRejectedEvent.getErrorMessage());
                    ViewUtils.showErrorMsg("Send post exception: ", new Exception(fileRejectedEvent.getErrorMessage()));
                }
        );
        uploadFile.addFailedListener(failedEvent -> log.warn("FailedListener --> {}", failedEvent.getReason().getMessage()));

        String maxFileSize = mainView.getEnv().getProperty("spring.servlet.multipart.max-file-size", "10MB");
        Integer fileSize = Integer.parseInt(maxFileSize.substring(0, maxFileSize.length() - 2)) * 1024 * 1024;

        uploadFile.setMaxFileSize(fileSize);
        uploadFile.setAcceptedFileTypes(uploadFileTypes.toArray(new String[0]));
        uploadFile.setWidth(94, Unit.PERCENTAGE);
        uploadFile.setHeight(75, Unit.PERCENTAGE);
        uploadFile.setAutoUpload(true);
        uploadFile.setMaxFiles(countFiles);
        uploadFile.setDropAllowed(true);
        return uploadFile;
    }

    private void uploadImage(InputStream fileData, String fileName) throws IOException {
        String formatName = FilenameUtils.getExtension(fileName);
        String uploadFileName = isCover
                ? "cover." + formatName
                : getName(valueOf(card.getId()), card.getTitle(), card.getCardType().getTitle(), formatName);

        if (isCover) {
            Collection<File> coverDirList = FileUtils.listFiles(new File(rootDir), null, true);
            for (File file : coverDirList) {
                FileUtils.delete(file);
            }
        }

        try (BufferedInputStream bis = new BufferedInputStream(fileData)) {
            BufferedImage inBufImg = ImageIO.read(bis);
            int maxH = Integer.parseInt(mainView.getEnv().getProperty("app.image.max.height", "600"));
            BufferedImage resizedImage = compressImage(inBufImg, maxH);
            log.info("Create image file: {}", this.rootDir + uploadFileName);
            File file = new File(this.rootDir + uploadFileName);
            ImageIO.write(resizedImage, formatName, file);
            log.info("Create image file: {}... OK", this.rootDir + uploadFileName);
        }
    }

    private void uploadFile(InputStream fileData, String uploadFileName) throws IOException {
        log.info("Create file: {}", this.rootDir + uploadFileName);
        FileUtils.copyInputStreamToFile(fileData, new File(this.rootDir + uploadFileName));
        log.info("Create file: {}... OK", this.rootDir + uploadFileName);
    }

    private void checkRootDir() {
        File dir = new File(this.rootDir);
        if (!dir.exists()) {
            log.info("Create root dir: {}", this.rootDir);
            dir.mkdirs();
            log.info("Create root dir: {}... OK", this.rootDir);
        }
    }

    private BufferedImage compressImage(BufferedImage image, int maxHeight) {
        int h = image.getHeight();
        int targetWidth = image.getWidth();
        int targetHeight = image.getHeight();

        if (h > maxHeight) {
            targetHeight = maxHeight;
            targetWidth = image.getWidth() * maxHeight / h;
        }
        return Scalr.resize(image, targetWidth, targetHeight);
    }

    private String replaceWord(String str, List<String> badWords, String newWord) {
        String rez = str;
        for (String badWord : badWords) {
            rez = rez.replace(badWord, newWord);
        }
        return rez;
    }

    private String getName(String prefix, String name, String suffix, String format) {
        String p = replaceWord(prefix.trim(), nameWordExc, "_");
        String n = replaceWord(name.trim(), nameWordExc, "_");
        String s = replaceWord(suffix.trim(), nameWordExc, "_");

        String rez = String.join("_", p, n, s) + "." + format;

        int number = 1;
        File file = new File(this.rootDir + rez);
        while (file.exists()) {
            rez = String.join("_", p, n, s, valueOf(number++)) + "." + format;
            file = new File(this.rootDir + rez);
        }

        return rez;
    }
}
