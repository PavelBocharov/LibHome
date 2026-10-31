package com.mar.ds.views;

import com.mar.ds.data.Page;
import com.mar.ds.utils.FileUtils;
import com.mar.ds.utils.ViewUtils;
import com.mar.ds.views.card.CardInfoView;
import com.mar.libhome.api.data.PageRequest;
import com.mar.libhome.dto.CardDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
public final class StartPageView implements ContentView {

    private final MainView mainView;
    @Getter
    private final FileUtils.ViewTypeDto viewType;

    public Component getContent() {
        try {

            ComboBox<CardDto> cardSearch = new ComboBox<>();
            cardSearch.setWidth(50, Unit.PERCENTAGE);
            cardSearch.setPlaceholder("Search");
            cardSearch.setItemLabelGenerator(card -> {
                Optional<FileUtils.ViewTypeDto> view = mainView.getViewTypeList().stream().filter(viewTypeDto -> viewTypeDto.id().equals(card.getViewType()))
                        .findFirst();
                return view
                        .map(viewTypeDto -> String.format("%s [%s]", card.getTitle(), viewTypeDto.title()))
                        .orElseGet(card::getTitle);
            });
            cardSearch.setClearButtonVisible(true);

            CallbackDataProvider<CardDto, String> provider = DataProvider.fromFilteringCallbacks(
                    query -> {
                        int offset = query.getOffset();
                        int limit = query.getLimit();
                        return Objects.requireNonNull(loadCards(query.getFilter())).getContent().stream();
                    },
                    query -> Math.toIntExact(Objects.requireNonNull(loadCards(query.getFilter())).getTotalCount()));

            cardSearch.setDataProvider(provider);
            cardSearch.addValueChangeListener(event -> {
                CardDto selected = event.getValue();
                if (selected != null) {
                    FileUtils.ViewTypeDto viewTypeInfo = mainView.getViewTypeList().stream()
                            .filter(Objects::nonNull)
                            .filter(viewTypeDto -> viewTypeDto.id().equals(selected.getViewType()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Not found viewType by id = " + selected.getViewType()));
                    CardInfoView cardInfoView = new CardInfoView(this.mainView, selected, viewTypeInfo);
                    cardInfoView.open();
                }
                cardSearch.clear();
            });

            Image image = new Image("imgs/background.jpg", "Alt text");
            VerticalLayout verticalLayout = new VerticalLayout(
                    new H3("LibHome - your book, game, music and other library."),
                    cardSearch,
                    image
            );
            verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            return verticalLayout;
        } catch (Exception e) {
            ViewUtils.showErrorMsg("ERROR: Load page.", e);
        }
        return null;
    }

    private Page<CardDto> loadCards(Optional<String> searchText) {
        String text = searchText.orElse(null);
        if (isBlank(text)) {
            return Page.<CardDto>builder()
                    .content(Collections.emptyList())
                    .totalCount(0)
                    .page(PageRequest.of(0, 0))
                    .build();
        }
        return mainView.getCardService().findAllByTextWithoutView(text, PageRequest.of(0, 15));
    }

}
