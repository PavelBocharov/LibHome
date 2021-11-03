package com.mar.randomvaadin.utils;

import com.google.common.io.Resources;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

@UtilityClass
public class ViewUtils {

    public static Image getImageByResource(String pathInResource) throws IOException {
        URL imgUrl = Resources.getResource(pathInResource);
        byte[] img = Resources.asByteSource(imgUrl).read();
        return new Image(
                new StreamResource(
                        FileUtils.getFile(imgUrl.getFile()).getName(),
                        () -> new ByteArrayInputStream(img)), String.format("Not load image: %s", pathInResource)
        );
    }

}
