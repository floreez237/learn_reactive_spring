package com.florian.learn_reactive_spring.routers;

import com.florian.learn_reactive_spring.constants.ItemConstants;
import com.florian.learn_reactive_spring.document.ItemCapped;
import com.florian.learn_reactive_spring.handlers.ItemCappedHandler;
import com.florian.learn_reactive_spring.handlers.ItemHandler;
import com.florian.learn_reactive_spring.handlers.SampleHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1;
import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_FUNCTIONAL_STREAM_ENDPOINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(SampleHandler sampleHandler) {
        return RouterFunctions.
                route(GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)),
                        sampleHandler::flux)
                .andRoute(GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)),
                        sampleHandler::mono);
    }

    @Bean
    public RouterFunction<ServerResponse> itemRouterFunction(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/all")).and(accept(MediaType.APPLICATION_JSON)), itemHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON)),
                        itemHandler::getItemById)
                .andRoute(POST(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/new")).and(accept(MediaType.APPLICATION_JSON)), itemHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/delete/{id}")),
                        itemHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/update")).and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)), itemHandler::updateItem);

    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET("/fun/exception"),itemHandler::itemEx);
    }

    @Bean
    public RouterFunction<ServerResponse> itemCappedRoutes(ItemCappedHandler itemCappedHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_STREAM_ENDPOINT_V1)
                        ,itemCappedHandler::cappedStream);
    }

}
