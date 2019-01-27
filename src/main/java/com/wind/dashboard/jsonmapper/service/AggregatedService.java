package com.wind.dashboard.jsonmapper.service;

import com.wind.dashboard.jsonmapper.enums.WidgetType;
import com.wind.dashboard.jsonmapper.enums.WidgetType;
import com.wind.dashboard.jsonmapper.model.dto.input.Bundle;
import com.wind.dashboard.jsonmapper.model.dto.input.Offer;
import com.wind.dashboard.jsonmapper.model.dto.input.UserData;
import com.wind.dashboard.jsonmapper.model.AggregatedWidgetFactory;
import com.wind.dashboard.jsonmapper.model.dto.aggregated.AggregatedDTO;
import com.wind.dashboard.jsonmapper.model.dto.aggregated.AggregatedWidget;
import com.wind.dashboard.jsonmapper.model.dto.aggregated.Header;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AggregatedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AggregatedService.class);
    private static AggregatedWidgetFactory factory = new AggregatedWidgetFactory();

    /**
     * @param data
     * @return
     */
    public AggregatedDTO getDashboard(UserData data) {
        LOGGER.info("Computing new Aggregated Dashboard");
        Map<WidgetType, AggregatedWidget> widgetMap = initMap();

        List<Offer> offers = data.getOffers();
        for (Offer offer : offers) {
            widgetMap = getWidgets(widgetMap, offer);
        }

        Header header = new Header(data.getMsisdn(), data.getCredit());
        AggregatedDTO response = AggregatedDTO.builder().header(header)
                .widgets(widgetMap).build();

        return response;
    }

    /**
     * @param widgetMap
     * @param offer
     * @return
     */
    private Map<WidgetType, AggregatedWidget> getWidgets(Map<WidgetType, AggregatedWidget> widgetMap, Offer offer) {
        LOGGER.warn("TODO");
        List<Bundle> bundles = offer.getBundles();
        widgetMap.entrySet().stream().forEach(element -> {
            element.setValue(getValues(element.getValue(), element.getKey(), bundles));
        });

        return widgetMap;
    }

    /**
     * @param widget
     * @param tag
     * @param bundles
     * @return
     */
    private AggregatedWidget getValues(AggregatedWidget widget, WidgetType tag, List<Bundle> bundles) {
        LOGGER.warn("TODO");
        Long residual = bundles.stream()
                .filter(bundle -> tag.toString().equalsIgnoreCase(bundle.getValue()))
                .mapToLong(Bundle::getResidual).sum();
        Long accumulated = bundles.stream()
                .filter(bundle -> tag.toString().equalsIgnoreCase(bundle.getValue()))
                .mapToLong(Bundle::getAccumulated).sum();

        Long total = widget.getTotal() + residual + accumulated;
        residual += widget.getResidual();

        widget.setTotal(total);
        widget.setResidual(residual);

        return widget;
    }

    /**
     * @return
     */
    private Map<WidgetType, AggregatedWidget> initMap() {
        Map<WidgetType, AggregatedWidget> widgetMap = new HashMap<>();
        Arrays.stream(WidgetType.values()).forEach(type -> {
            AggregatedWidget widget = factory.create(type);
            widgetMap.put(type, widget);
        });

        return widgetMap;
    }

}
